package com.example.david.lists.view.reauthentication.phone

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.reauthentication.phone.IPhoneReAuthContract.ViewEvent
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


    private val countryCode = "+1"
    private val validPhoneNum = "1235550100"
    private val formattedPhoneNum = "$countryCode$validPhoneNum"

    private val verificationId = "verificationId"

    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


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
         * - Display message that SMS has been sent.
         * - Display SMS view with the phone number from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - valid number - sent sms`() {
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

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            capturedCallbacks.captured.onCodeSent(verificationId, forceResendingToken)

            verify { viewModel.phoneNumber = formattedPhoneNum }
            verify { userRepo.validatePhoneNumber(formattedPhoneNum, capturedCallbacks.captured) }
            verify { view.displayMessage(message) }
            verify { view.openSmsVerification(formattedPhoneNum, verificationId) }
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
}