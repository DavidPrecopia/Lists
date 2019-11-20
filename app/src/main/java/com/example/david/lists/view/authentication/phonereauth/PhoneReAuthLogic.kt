package com.example.david.lists.view.authentication.phonereauth

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.data.repository.SMS_TIME_OUT_SECONDS
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.authentication.phonereauth.IPhoneReAuthContract.ViewEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

private const val VALID_PHONE_NUM_LENGTH = 10
private const val PHONE_NUM_COUNTRY_CODE_USA = "+1"

class PhoneReAuthLogic(private val view: IPhoneReAuthContract.View,
                       private val viewModel: IPhoneReAuthContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository) :
        IPhoneReAuthContract.Logic {

    override fun onEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.ConfirmPhoneNumClicked -> evalPhoneNum(event.phoneNum.trim())
            is ViewEvent.ConfirmSmsClicked -> evalSmsCode(event.sms.trim())
            ViewEvent.TimerFinished -> reSentSms()
        }
    }


    private fun evalPhoneNum(phoneNum: String) {
        when {
            numIsInvalid(phoneNum) -> view.displayError(viewModel.msgInvalidNum)
            else -> verifyPhoneNum(phoneNum)
        }
    }

    private fun numIsInvalid(phoneNum: String) = with(phoneNum) {
        isBlank() || length != VALID_PHONE_NUM_LENGTH || notExclusivelyDigits(phoneNum)
    }

    private fun verifyPhoneNum(phoneNum: String) {
        val formattedNum = formatPhoneNum(phoneNum)

        viewModel.phoneNumber = formattedNum

        userRepo.validatePhoneNumber(
                formattedNum,
                verifyPhoneNumCallbacks()
        )
    }

    private fun formatPhoneNum(phoneNum: String) = "$PHONE_NUM_COUNTRY_CODE_USA$phoneNum"

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
            view.displayMessage(viewModel.msgGenericError)
            view.finishView()
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            viewModel.verificationId = verificationId
            view.displayMessage(viewModel.msgSmsSent)
            view.displaySmsVerification()
            view.startTimer(SMS_TIME_OUT_SECONDS)
        }
    }


    private fun evalSmsCode(smsCode: String) {
        when {
            smsCodeIsInvalid(smsCode) -> view.displayError(viewModel.msgInvalidSms)
            else -> deleteAccount(smsCode)
        }
    }

    private fun smsCodeIsInvalid(smsCode: String) = with(smsCode) {
        isBlank() || notExclusivelyDigits(smsCode)
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
        view.displayMessage(viewModel.msgAccountDeletionSucceed)
        view.openAuthView()
    }

    private fun deletionFailed() = OnFailureListener { e ->
        UtilExceptions.throwException(e)
        view.displayMessage(viewModel.msgAccountDeletionFailed)
        view.displayError(viewModel.msgReEnterSms)
    }


    private fun reSentSms() {
        userRepo.validatePhoneNumber(
                viewModel.phoneNumber,
                verifyPhoneNumCallbacks()
        )
    }


    private fun notExclusivelyDigits(string: String) = string.matches(Regex("[0-9]+")).not()
}