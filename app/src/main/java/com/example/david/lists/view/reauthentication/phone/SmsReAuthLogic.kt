package com.example.david.lists.view.reauthentication.phone

import com.example.david.lists.common.onlyDigits
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvent
import com.example.domain.constants.SMS_TIME_OUT_SECONDS
import com.example.domain.repository.IRepositoryContract
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class SmsReAuthLogic(private val view: ISmsReAuthContract.View,
                     private val viewModel: ISmsReAuthContract.ViewModel,
                     private val userRepo: IRepositoryContract.UserRepository) :
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
        userRepo.deletePhoneUser(
                viewModel.verificationId,
                smsCode,
                deletionSucceeded(),
                deletionFailed()
        )
    }

    private fun deletionSucceeded() = OnSuccessListener<Void> {
        view.cancelTimer()
        view.displayMessage(viewModel.msgAccountDeletionSucceed)
        view.openAuthView()
    }

    private fun deletionFailed() = OnFailureListener { e ->
        UtilExceptions.throwException(e)
        evalDeletionFailureException(e)
    }

    private fun evalDeletionFailureException(e: Exception) {
        when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
                view.hideLoading()
                view.displayError(viewModel.msgInvalidSms)
            }
            is FirebaseTooManyRequestsException -> {
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
        userRepo.validatePhoneNumber(
                viewModel.phoneNumber,
                verificationCallbacks()
        )
    }

    private fun verificationCallbacks() = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        /**
         * This is called when the user is instantly verified, thus an SMS code is not sent.
         * In this case, I cannot continue because, unlike [onCodeSent], this method does not give me the Verification ID.
         */
        override fun onVerificationCompleted(authCredential: PhoneAuthCredential) {
            finish(
                    Exception("Phone user was instantly verified during deletion."),
                    viewModel.msgTryAgainLater
            )
        }

        /**
         * This cannot fail because of an invalid phone number (num has already been verified),
         * thus I can assume that it is due to an error out of the user's control.
         */
        override fun onVerificationFailed(e: FirebaseException) {
            finish(e, viewModel.msgGenericError)
        }

        private fun finish(e: Exception, message: String) {
            UtilExceptions.throwException(e)
            view.displayMessage(message)
            view.cancelTimer()
            view.finishView()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            viewModel.verificationId = verificationId
            view.displayMessage(viewModel.msgSmsSent)
            view.startTimer(SMS_TIME_OUT_SECONDS)
        }
    }
}