package com.precopia.david.lists.view.reauthentication.phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.precopia.david.lists.common.onlyDigits
import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.common.subscribeSingleValidatePhoneNum
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.reauthentication.phone.ISmsReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvents
import com.precopia.domain.constants.PhoneNumValidationResults
import com.precopia.domain.constants.SMS_TIME_OUT_SECONDS
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract

class SmsReAuthLogic(private val viewModel: ISmsReAuthContract.ViewModel,
                     private val userRepo: IRepositoryContract.UserRepository,
                     private val schedulerProvider: ISchedulerProviderContract) :
        ViewModel(),
        ISmsReAuthContract.Logic {


    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(event: LogicEvents) {
        when (event) {
            is LogicEvents.OnStart -> saveData(event.phoneNum, event.verificationId, event.timeLeft)
            is LogicEvents.ConfirmSmsClicked -> evalSmsCode(event.sms.trim())
            LogicEvents.TimerFinished -> reSentSms()
            LogicEvents.ViewDestroyed -> viewEvent(ViewEvents.CancelTimer)
        }
    }


    private fun saveData(phoneNum: String, verificationId: String, timeLeft: Long) {
        viewModel.phoneNumber = phoneNum
        smsCodeSent(verificationId, timeLeft)
    }


    private fun evalSmsCode(smsCode: String) {
        when {
            smsCodeIsInvalid(smsCode) -> viewEvent(
                    ViewEvents.DisplayError(viewModel.msgInvalidSms)
            )
            else -> {
                viewEvent(ViewEvents.DisplayLoading)
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
        viewEvent(ViewEvents.CancelTimer)
        viewEvent(ViewEvents.DisplayMessage(viewModel.msgAccountDeletionSucceed))
        viewEvent(ViewEvents.OpenAuthView)
    }

    private fun deletionFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        evalDeletionFailureException(e)
    }

    private fun evalDeletionFailureException(e: Throwable) {
        when (e) {
            is AuthInvalidCredentialsException -> {
                viewEvent(ViewEvents.HideLoading)
                viewEvent(ViewEvents.DisplayError(viewModel.msgInvalidSms))
            }
            is AuthTooManyRequestsException -> {
                viewEvent(ViewEvents.DisplayMessage(viewModel.msgTooManyRequest))
                viewEvent(ViewEvents.FinishView)
            }
            else -> {
                viewEvent(ViewEvents.DisplayMessage(viewModel.msgAccountDeletionFailed))
                viewEvent(ViewEvents.FinishView)
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
            is PhoneNumValidationResults.SmsSent -> smsCodeSent(results.validationCode, -1L)
            PhoneNumValidationResults.Validated -> verificationCompleted()
        }
    }

    private fun smsCodeSent(verificationId: String, timeLeft: Long) {
        viewModel.verificationId = verificationId
        if (timeLeft > 0) {
            viewEvent(ViewEvents.StartTimer(timeLeft))
        } else {
            viewEvent(ViewEvents.DisplayMessage(viewModel.msgSmsSent))
            viewEvent(ViewEvents.StartTimer(SMS_TIME_OUT_SECONDS))
        }
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
        viewEvent(ViewEvents.DisplayMessage(message))
        viewEvent(ViewEvents.CancelTimer)
        viewEvent(ViewEvents.FinishView)
    }


    private fun viewEvent(event: ViewEvents) {
        viewEventLiveData.value = event
    }

    override fun observe(): LiveData<ViewEvents> = viewEventLiveData
}