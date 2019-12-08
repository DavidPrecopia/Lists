package com.example.david.lists.view.reauthentication.phone

import com.example.david.lists.common.onlyDigits
import com.example.david.lists.common.subscribeSingleValidatePhoneNum
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.phone.IPhoneReAuthContract.ViewEvent
import com.example.domain.constants.PhoneNumValidationResults
import com.example.domain.constants.PhoneNumValidationResults.SmsSent
import com.example.domain.constants.PhoneNumValidationResults.Validated
import com.example.domain.exception.AuthInvalidCredentialsException
import com.example.domain.exception.AuthTooManyRequestsException
import com.example.domain.repository.IRepositoryContract

private const val VALID_PHONE_NUM_LENGTH = 10

class PhoneReAuthLogic(private val view: IPhoneReAuthContract.View,
                       private val viewModel: IPhoneReAuthContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository,
                       private val schedulerProvider: ISchedulerProviderContract) :
        IPhoneReAuthContract.Logic {

    override fun onEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.ConfirmPhoneNumClicked -> evalPhoneNum(event.phoneNum.trim())
        }
    }


    private fun evalPhoneNum(phoneNum: String) {
        when {
            numIsInvalid(phoneNum) -> view.displayError(viewModel.msgInvalidNum)
            else -> {
                viewModel.phoneNumber = phoneNum
                view.displayLoading()
                verifyPhoneNum(phoneNum)
            }
        }
    }

    private fun numIsInvalid(phoneNum: String) = with(phoneNum) {
        isBlank() || length != VALID_PHONE_NUM_LENGTH || onlyDigits.not()
    }


    private fun verifyPhoneNum(phoneNum: String) {
        subscribeSingleValidatePhoneNum(
                userRepo.validatePhoneNumber(phoneNum),
                { evalVerification(it) },
                { verificationFailed(it) },
                schedulerProvider
        )
    }

    private fun evalVerification(results: PhoneNumValidationResults) {
        when (results) {
            is SmsSent -> smsCodeSent(results.validationCode)
            Validated -> onVerificationCompleted()
        }
    }

    private fun smsCodeSent(verificationId: String) {
        view.displayMessage(viewModel.msgSmsSent)
        view.openSmsVerification(viewModel.phoneNumber, verificationId)
    }

    private fun onVerificationCompleted() {
        UtilExceptions.throwException(Exception("Phone user was instantly verified during deletion."))
        view.displayMessage(viewModel.msgTryAgainLater)
        view.finishView()
    }

    private fun verificationFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        evalFailureException(e)
    }

    private fun evalFailureException(e: Throwable) {
        when (e) {
            is AuthInvalidCredentialsException -> {
                view.hideLoading()
                view.displayError(viewModel.msgInvalidNum)
            }
            is AuthTooManyRequestsException -> {
                view.displayMessage(viewModel.msgTooManyRequest)
                view.finishView()
            }
            else -> {
                view.displayMessage(viewModel.msgGenericError)
                view.finishView()
            }
        }
    }
}