package com.precopia.david.lists.view.reauthentication.phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.precopia.david.lists.common.onlyDigits
import com.precopia.david.lists.common.subscribeSingleValidatePhoneNum
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract.ViewEvents
import com.precopia.domain.constants.PhoneNumValidationResults
import com.precopia.domain.constants.PhoneNumValidationResults.SmsSent
import com.precopia.domain.constants.PhoneNumValidationResults.Validated
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract

private const val VALID_PHONE_NUM_LENGTH = 10

class PhoneReAuthLogic(private val viewModel: IPhoneReAuthContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository,
                       private val schedulerProvider: ISchedulerProviderContract) :
        ViewModel(),
        IPhoneReAuthContract.Logic {

    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(event: LogicEvents) {
        when (event) {
            is LogicEvents.ConfirmPhoneNumClicked -> evalPhoneNum(event.phoneNum.trim())
        }
    }


    private fun evalPhoneNum(phoneNum: String) {
        when {
            numIsInvalid(phoneNum) -> viewEvent(
                    ViewEvents.DisplayError(viewModel.msgInvalidNum)
            )
            else -> {
                viewModel.phoneNumber = phoneNum
                viewEvent(ViewEvents.DisplayLoading)
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
        viewEvent(ViewEvents.DisplayMessage(viewModel.msgSmsSent))
        viewEvent(ViewEvents.OpenSmsVerification(viewModel.phoneNumber, verificationId))
    }

    private fun onVerificationCompleted() {
        UtilExceptions.throwException(Exception("Phone user was instantly verified during deletion."))
        viewEvent(ViewEvents.DisplayMessage(viewModel.msgTryAgainLater))
        viewEvent(ViewEvents.FinishView)
    }

    private fun verificationFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        evalFailureException(e)
    }

    private fun evalFailureException(e: Throwable) {
        when (e) {
            is AuthInvalidCredentialsException -> {
                viewEvent(ViewEvents.HideLoading)
                viewEvent(ViewEvents.DisplayError(viewModel.msgInvalidNum))
            }
            is AuthTooManyRequestsException -> {
                viewEvent(ViewEvents.DisplayMessage(viewModel.msgTooManyRequest))
                viewEvent(ViewEvents.FinishView)
            }
            else -> {
                viewEvent(ViewEvents.DisplayError(viewModel.msgGenericError))
                viewEvent(ViewEvents.FinishView)
            }
        }
    }


    private fun viewEvent(event: ViewEvents) {
        viewEventLiveData.value = event
    }

    override fun observe(): LiveData<ViewEvents> = viewEventLiveData
}