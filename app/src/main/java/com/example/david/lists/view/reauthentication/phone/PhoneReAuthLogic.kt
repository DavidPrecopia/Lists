package com.example.david.lists.view.reauthentication.phone

import com.example.david.lists.common.onlyDigits
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.phone.IPhoneReAuthContract.ViewEvent
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

private const val VALID_PHONE_NUM_LENGTH = 10

class PhoneReAuthLogic(private val view: IPhoneReAuthContract.View,
                       private val viewModel: IPhoneReAuthContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository) :
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
        userRepo.validatePhoneNumber(
                phoneNum,
                verifyPhoneNumCallbacks()
        )
    }

    private fun verifyPhoneNumCallbacks() = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        /**
         * This is called when the user is instantly verified, thus an SMS code is not sent.
         * In this case, I cannot continue because, unlike [onCodeSent], this method does not give me the Verification ID.
         */
        override fun onVerificationCompleted(authCredential: PhoneAuthCredential) {
            UtilExceptions.throwException(Exception("Phone user was instantly verified during deletion."))
            view.displayMessage(viewModel.msgTryAgainLater)
            view.finishView()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            UtilExceptions.throwException(e)
            evalFailureException(e)

        }

        private fun evalFailureException(e: FirebaseException) {
            when (e) {
                is FirebaseAuthInvalidCredentialsException -> {
                    view.hideLoading()
                    view.displayError(viewModel.msgInvalidNum)
                }
                is FirebaseTooManyRequestsException -> {
                    view.displayMessage(viewModel.msgTooManyRequest)
                    view.finishView()
                }
                else -> {
                    view.displayMessage(viewModel.msgGenericError)
                    view.finishView()
                }
            }
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            view.displayMessage(viewModel.msgSmsSent)
            view.openSmsVerification(viewModel.phoneNumber, verificationId)
        }
    }
}