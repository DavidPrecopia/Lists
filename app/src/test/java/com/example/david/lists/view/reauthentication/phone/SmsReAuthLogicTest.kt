package com.example.david.lists.view.reauthentication.phone

import com.example.androiddata.repository.SMS_TIME_OUT_SECONDS
import com.example.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvent
import com.example.domain.repository.IRepositoryContract
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SmsReAuthLogicTest {

    private val view = mockk<ISmsReAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<ISmsReAuthContract.ViewModel>(relaxUnitFun = true)

    private val userRepo = mockk<IRepositoryContract.UserRepository>(relaxUnitFun = true)


    private val logic = SmsReAuthLogic(view, viewModel, userRepo)


    private val validPhoneNum = "1235550100"

    private val verificationId = "verificationId"

    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    @Nested
    inner class OnStart {
        /**
         * - [ViewEvent.OnStart]
         * - Save the phone number and the verification ID to the ViewModel.
         */
        @Test
        fun onStart() {
            logic.onEvent(ViewEvent.OnStart(validPhoneNum, verificationId))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { viewModel.verificationId = verificationId }
        }
    }


    @Nested
    inner class ConfirmSms {

        private val validSms = "123456"

        /**
         * - [ViewEvent.ConfirmSmsClicked]
         * - Validate the SMS number
         *   - It will be valid.
         * - Display loading.
         * - Delete the account via the UserRepo with the verification ID from the ViewModel.
         *   - It will be successful.
         * - Cancel the timer.
         * - Display a message from the ViewModel.
         * - Open the auth view.
         */
        @Test
        fun `onEvent - confirm sms - valid sms - successful`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgAccountDeletionSucceed } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms,
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.ConfirmSmsClicked(validSms))

            captureArgSuccess.captured.onSuccess(null)

            verify { view.displayLoading() }
            verify { userRepo.deletePhoneUser(verificationId, validSms, captureArgSuccess.captured, captureArgFailure.captured) }
            verify { view.cancelTimer() }
            verify { view.displayMessage(message) }
            verify { view.openAuthView() }
        }

        /**
         * - [ViewEvent.ConfirmSmsClicked]
         * - Validate the SMS number
         *   - It will be valid.
         * - Display loading.
         * - Delete the account via the UserRepo with the verification ID from the ViewModel.
         *   - It will fail with [FirebaseAuthInvalidCredentialsException].
         * - Thrown an Exception.
         * - Hide loading.
         * - Display a error message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm sms - valid sms - failure - invalid credentials`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()
            val exception = mockk<FirebaseAuthInvalidCredentialsException>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgInvalidSms } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms,
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.ConfirmSmsClicked(validSms))

            captureArgFailure.captured.onFailure(exception)

            verify { view.displayLoading() }
            verify { userRepo.deletePhoneUser(verificationId, validSms, captureArgSuccess.captured, captureArgFailure.captured) }
            verify { exception.printStackTrace() }
            verify { view.hideLoading() }
            verify { view.displayError(message) }
        }

        /**
         * - [ViewEvent.ConfirmSmsClicked]
         * - Validate the SMS number
         *   - It will be valid.
         * - Display loading.
         * - Delete the account via the UserRepo with the verification ID from the ViewModel.
         *   - It will fail with [FirebaseTooManyRequestsException].
         * - Thrown an Exception.
         * - Display a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - confirm sms - valid sms - failure - too many requests`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()
            val exception = mockk<FirebaseTooManyRequestsException>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgTooManyRequest } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms,
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.ConfirmSmsClicked(validSms))

            captureArgFailure.captured.onFailure(exception)

            verify { view.displayLoading() }
            verify { userRepo.deletePhoneUser(verificationId, validSms, captureArgSuccess.captured, captureArgFailure.captured) }
            verify { exception.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.ConfirmSmsClicked]
         * - Validate the SMS number
         *   - It will be valid.
         * - Display loading.
         * - Delete the account via the UserRepo with the verification ID from the ViewModel.
         *   - It will fail with an general Exception.
         * - Thrown an Exception.
         * - Display a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - confirm sms - valid sms - failure - general exceptions`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgAccountDeletionFailed } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms,
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.ConfirmSmsClicked(validSms))

            captureArgFailure.captured.onFailure(exception)

            verify { view.displayLoading() }
            verify { userRepo.deletePhoneUser(verificationId, validSms, captureArgSuccess.captured, captureArgFailure.captured) }
            verify { exception.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.ConfirmSmsClicked]
         * - Validate the SMS number.
         *   - It will be invalid because it is empty.
         * - Display a error message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm sms - invalid sms - empty`() {
            val emptySms = ""

            every { viewModel.msgInvalidSms } returns message

            logic.onEvent(ViewEvent.ConfirmSmsClicked(emptySms))

            verify { view.displayError(message) }
            verify { userRepo wasNot Called }
        }

        /**
         * - [ViewEvent.ConfirmSmsClicked]
         * - Validate the SMS number.
         *   - It will be invalid because it contains a letter.
         * - Display a error message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm sms - invalid sms - contains a letter`() {
            val smsWithLetter = validSms + "L"

            every { viewModel.msgInvalidSms } returns message

            logic.onEvent(ViewEvent.ConfirmSmsClicked(smsWithLetter))

            verify { view.displayError(message) }
            verify { userRepo wasNot Called }
        }
    }


    @Nested
    inner class TimerFinished {
        /**
         * - [ViewEvent.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - This will be successful.
         * - Save the validation ID to the ViewModel.
         * - Display message that SMS has been sent.
         * - Display SMS verification.
         * - Start timer.
         */
        @Test
        fun `onEvent - timer finished - re-sent sms`() {
            val capturedCallbacks = CapturingSlot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
            val forceResendingToken = mockk<PhoneAuthProvider.ForceResendingToken>(relaxed = true)

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgSmsSent } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = validPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.TimerFinished)

            capturedCallbacks.captured.onCodeSent(verificationId, forceResendingToken)

            verify { userRepo.validatePhoneNumber(validPhoneNum, capturedCallbacks.captured) }
            verify { viewModel.verificationId = verificationId }
            verify { view.displayMessage(message) }
            verify { view.startTimer(SMS_TIME_OUT_SECONDS) }
        }

        /**
         * - [ViewEvent.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - This will fail - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationFailed] will be called.
         * - Throw an Exception.
         * - Display an error with a message from the ViewModel.
         * - Cancel the Timer.
         * - Finish the View.
         */
        @Test
        fun `onEvent - timer finished - failed`() {
            val capturedCallbacks = CapturingSlot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
            val firebaseException = mockk<FirebaseException>(relaxed = true)

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgGenericError } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = validPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.TimerFinished)

            capturedCallbacks.captured.onVerificationFailed(firebaseException)

            verify { userRepo.validatePhoneNumber(validPhoneNum, capturedCallbacks.captured) }
            verify { firebaseException.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.cancelTimer() }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - User will be instantly verified - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationCompleted] will be called.
         * - Thrown an Exception.
         * - Display a message from the ViewModel.
         * - Cancel the Timer.
         * - Finish the View.
         */
        @Test
        fun `onEvent - timer finished - verification completed`() {
            val capturedCallbacks = CapturingSlot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
            val phoneAuthCredential = mockk<PhoneAuthCredential>(relaxed = true)

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgTryAgainLater } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = validPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.TimerFinished)

            capturedCallbacks.captured.onVerificationCompleted(phoneAuthCredential)

            verify { userRepo.validatePhoneNumber(validPhoneNum, capturedCallbacks.captured) }
            verify { view.displayMessage(message) }
            verify { view.cancelTimer() }
            verify { view.finishView() }
        }
    }
}