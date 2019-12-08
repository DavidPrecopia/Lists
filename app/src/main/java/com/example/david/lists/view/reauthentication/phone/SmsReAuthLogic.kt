package com.example.david.lists.view.reauthentication.phone

import com.example.david.lists.common.onlyDigits
import com.example.david.lists.common.subscribeCompletable
import com.example.david.lists.common.subscribeSingleValidatePhoneNum
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvent
import com.example.domain.constants.PhoneNumValidationResults
import com.example.domain.constants.SMS_TIME_OUT_SECONDS
import com.example.domain.exception.AuthInvalidCredentialsException
import com.example.domain.exception.AuthTooManyRequestsException
import com.example.domain.repository.IRepositoryContract

class SmsReAuthLogic(private val view: ISmsReAuthContract.View,
                     private val viewModel: ISmsReAuthContract.ViewModel,
                     private val userRepo: IRepositoryContract.UserRepository,
                     private val schedulerProvider: ISchedulerProviderContract) :
        ISmsReAuthContract.Logic {


    override fun onEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.OnStart -> saveData(event.phoneNum, event.verificationId)
            is ViewEvent.ConfirmSmsClicked -> evalSmsCode(event.sms.trim())
            ViewEvent.TimerFinished -> reSentSms()
        }
    }


    private fun saveData(phoneNum: String, verificationId: String) {
        viewModel.phoneNumber = phoneNum
        viewModel.verificationId = verificationId
    }


    private fun evalSmsCode(smsCode: String) {
        when {
            smsCodeIsInvalid(smsCode) -> view.displayError(viewModel.msgInvalidSms)
            else -> {
                view.displayLoading()
                deleteAccount(smsCode)
            }
        }
    }

    private fun smsCodeIsInvalid(smsCode: String) = with(smsCode) {
        isBlank() || onlyDigits.not()
    }

    private fun deleteAccount(smsCode: String) {
        subscribeCompletable(
                userRepo.deletePhoneUser(viewModel.verificationId, smsCode),
                { deletionSucceeded() },
                { deletionFailed(it) },
                schedulerProvider
        )
    }

    private fun deletionSucceeded() {
        view.cancelTimer()
        view.displayMessage(viewModel.msgAccountDeletionSucceed)
        view.openAuthView()
    }

    private fun deletionFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        evalDeletionFailureException(e)
    }

    private fun evalDeletionFailureException(e: Throwable) {
        when (e) {
            is AuthInvalidCredentialsException -> {
                view.hideLoading()
                view.displayError(viewModel.msgInvalidSms)
            }
            is AuthTooManyRequestsException -> {
                view.displayMessage(viewModel.msgTooManyRequest)
                view.finishView()
            }
            else -> {
                view.displayMessage(viewModel.msgAccountDeletionFailed)
                view.finishView()
            }
        }
    }


    private fun reSentSms() {
        subscribeSingleValidatePhoneNum(
                userRepo.validatePhoneNumber(viewModel.phoneNumber),
                { evalVerification(it) },
                { verificationFailed(it) },
                schedulerProvider
        )
    }

    private fun evalVerification(results: PhoneNumValidationResults) {
        when (results) {
            is PhoneNumValidationResults.SmsSent -> smsCodeSent(results.validationCode)
            PhoneNumValidationResults.Validated -> verificationCompleted()
        }
    }

    private fun smsCodeSent(verificationId: String) {
        viewModel.verificationId = verificationId
        view.displayMessage(viewModel.msgSmsSent)
        view.startTimer(SMS_TIME_OUT_SECONDS)
    }


    private fun verificationCompleted() {
        finish(
                Exception("Phone user was instantly verified during deletion."),
                viewModel.msgTryAgainLater
        )
    }

    private fun verificationFailed(e: Throwable) {
        finish(e, evalFailureException(e))
    }

    private fun evalFailureException(e: Throwable) = when (e) {
        is AuthTooManyRequestsException -> viewModel.msgTooManyRequest
        else -> viewModel.msgGenericError
    }


    private fun finish(e: Throwable, message: String) {
        UtilExceptions.throwException(e)
        view.displayMessage(message)
        view.cancelTimer()
        view.finishView()
    }
}