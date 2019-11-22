package com.example.david.lists.view.reauthentication.phone

import com.example.david.lists.common.onlyDigits
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.data.repository.SMS_TIME_OUT_SECONDS
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
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
        with(view) {
            hideLoading()
            displayMessage(viewModel.msgAccountDeletionFailed)
            displayError(viewModel.msgReEnterSms)
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
            UtilExceptions.throwException(Exception("Phone user was instantly verified during deletion."))
            view.displayMessage(viewModel.msgTryAgainLater)
            view.cancelTimer()
            view.finishView()
        }

        override fun onVerificationFailed(e: FirebaseException) {
            UtilExceptions.throwException(e)
            view.displayMessage(viewModel.msgGenericError)
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