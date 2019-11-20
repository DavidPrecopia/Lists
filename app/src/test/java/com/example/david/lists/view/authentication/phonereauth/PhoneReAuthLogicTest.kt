package com.example.david.lists.view.authentication.phonereauth

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.data.repository.SMS_TIME_OUT_SECONDS
import com.example.david.lists.view.authentication.phonereauth.IPhoneReAuthContract.ViewEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PhoneReAuthLogicTest {

    private val view = mockk<IPhoneReAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IPhoneReAuthContract.ViewModel>(relaxUnitFun = true)

    private val userRepo = mockk<IRepositoryContract.UserRepository>(relaxUnitFun = true)


    private val logic = PhoneReAuthLogic(view, viewModel, userRepo)


    private val verificationId = "verificationId"

    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    private val countryCode = "+1"
    private val validPhoneNum = "1235550100"
    private val formattedPhoneNum = "$countryCode$validPhoneNum"

    @Nested
    inner class ConfirmPhoneNumber {
        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be valid.
         * - Save phone number to ViewModel.
         * - Verify phone number via UserRepo.
         *   - It will be valid - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onCodeSent] will be called.
         * - Save the validation ID to the ViewModel.
         * - Display message that SMS has been sent.
         * - Display SMS verification.
         * - Start timer with [SMS_TIME_OUT_SECONDS].
         */
        @Test
        fun `onEvent - confirm phone num - valid number - sent sms`() {
            val capturedCallbacks = CapturingSlot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
            val forceResendingToken = mockk<PhoneAuthProvider.ForceResendingToken>(relaxed = true)

            every { viewModel.msgSmsSent } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = formattedPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            capturedCallbacks.captured.onCodeSent(verificationId, forceResendingToken)

            verify { viewModel.phoneNumber = formattedPhoneNum }
            verify { userRepo.validatePhoneNumber(formattedPhoneNum, capturedCallbacks.captured) }
            verify { viewModel.verificationId = verificationId }
            verify { view.displayMessage(message) }
            verify { view.displaySmsVerification() }
            verify { view.startTimer(SMS_TIME_OUT_SECONDS) }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be valid.
         * - Save phone number to ViewModel.
         * - Verify phone number via UserRepo.
         *   - This will fail - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationFailed] will be called.
         * - Throw an Exception.
         * - Display an error with a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - confirm phone num - valid number - failed`() {
            val capturedCallbacks = CapturingSlot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
            val firebaseException = mockk<FirebaseException>(relaxed = true)

            every { viewModel.msgGenericError } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = formattedPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            capturedCallbacks.captured.onVerificationFailed(firebaseException)

            verify { viewModel.phoneNumber = formattedPhoneNum }
            verify { userRepo.validatePhoneNumber(formattedPhoneNum, capturedCallbacks.captured) }
            verify { firebaseException.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be valid for this test.
         * - Save phone number to ViewModel.
         * - Verify phone number via UserRepo.
         *   - User will be instantly verified - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationCompleted] will be called.
         * - Thrown an Exception.
         * - Display a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - confirm phone num - valid number - verification completed`() {
            val capturedCallbacks = CapturingSlot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
            val phoneAuthCredential = mockk<PhoneAuthCredential>(relaxed = true)

            every { viewModel.msgTryAgainLater } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = formattedPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            capturedCallbacks.captured.onVerificationCompleted(phoneAuthCredential)

            verify { viewModel.phoneNumber = formattedPhoneNum }
            verify { userRepo.validatePhoneNumber(formattedPhoneNum, capturedCallbacks.captured) }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }


        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be invalid because it is empty.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - invalid number - empty`() {
            val emptyPhoneNum = ""

            every { viewModel.msgInvalidNum } returns message

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(emptyPhoneNum))

            verify { view.displayError(message) }
            verify { userRepo wasNot Called }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be invalid because it contains a letter.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - invalid number - contains a letter`() {
            val phoneNumWithLetter = validPhoneNum + "L"

            every { viewModel.msgInvalidNum } returns message

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(phoneNumWithLetter))

            verify { view.displayError(message) }
            verify { userRepo wasNot Called }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be invalid because it too short.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - invalid number - too short`() {
            val shortPhoneNum = "123"

            every { viewModel.msgInvalidNum } returns message

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(shortPhoneNum))

            verify { view.displayError(message) }
            verify { userRepo wasNot Called }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be invalid because it too long.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - invalid number - too long`() {
            val longPhoneNum = Long.MAX_VALUE.toString()

            every { viewModel.msgInvalidNum } returns message

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(longPhoneNum))

            verify { view.displayError(message) }
            verify { userRepo wasNot Called }
        }
    }

    @Nested
    inner class ConfirmSms {

        private val validSms = "123456"

        /**
         * - [ViewEvent.ConfirmSmsClicked]
         * - Validate the SMS number
         *   - It will be valid.
         * - Delete the account via the UserRepo with the verification ID from the ViewModel.
         *   - It will be successful.
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

            verify { userRepo.deletePhoneUser(verificationId, validSms, captureArgSuccess.captured, captureArgFailure.captured) }
            verify { view.displayMessage(message) }
            verify { view.openAuthView() }
        }

        /**
         * - [ViewEvent.ConfirmSmsClicked]
         * - Validate the SMS number
         *   - It will be valid.
         * - Delete the account via the UserRepo with the verification ID from the ViewModel.
         *   - It will fail.
         * - Thrown an Exception.
         * - Display a message from the ViewModel.
         * - Display an error message from the ViewModel ask user to re-enter the SMS code.
         */
        @Test
        fun `onEvent - confirm sms - valid sms - failure`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgAccountDeletionFailed } returns message
            every { viewModel.msgReEnterSms } returns message
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

            verify { userRepo.deletePhoneUser(verificationId, validSms, captureArgSuccess.captured, captureArgFailure.captured) }
            verify { exception.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.displayError(message) }
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

            every { viewModel.phoneNumber } returns formattedPhoneNum
            every { viewModel.msgSmsSent } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = formattedPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.TimerFinished)

            capturedCallbacks.captured.onCodeSent(verificationId, forceResendingToken)

            verify { userRepo.validatePhoneNumber(formattedPhoneNum, capturedCallbacks.captured) }
            verify { viewModel.verificationId = verificationId }
            verify { view.displayMessage(message) }
            verify { view.displaySmsVerification() }
            verify { view.startTimer(SMS_TIME_OUT_SECONDS) }
        }

        /**
         * - [ViewEvent.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - This will fail - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationFailed] will be called.
         * - Throw an Exception.
         * - Display an error with a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - timer finished - failed`() {
            val capturedCallbacks = CapturingSlot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
            val firebaseException = mockk<FirebaseException>(relaxed = true)

            every { viewModel.phoneNumber } returns formattedPhoneNum
            every { viewModel.msgGenericError } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = formattedPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.TimerFinished)

            capturedCallbacks.captured.onVerificationFailed(firebaseException)

            verify { userRepo.validatePhoneNumber(formattedPhoneNum, capturedCallbacks.captured) }
            verify { firebaseException.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - User will be instantly verified - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationCompleted] will be called.
         * - Thrown an Exception.
         * - Display a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - timer finished - verification completed`() {
            val capturedCallbacks = CapturingSlot<PhoneAuthProvider.OnVerificationStateChangedCallbacks>()
            val phoneAuthCredential = mockk<PhoneAuthCredential>(relaxed = true)

            every { viewModel.phoneNumber } returns formattedPhoneNum
            every { viewModel.msgTryAgainLater } returns message
            every {
                userRepo.validatePhoneNumber(
                        callbacks = capture(capturedCallbacks),
                        phoneNum = formattedPhoneNum
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.TimerFinished)

            capturedCallbacks.captured.onVerificationCompleted(phoneAuthCredential)

            verify { userRepo.validatePhoneNumber(formattedPhoneNum, capturedCallbacks.captured) }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }
    }
}