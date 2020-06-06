package com.precopia.david.lists.view.reauthentication.phone

import com.google.firebase.auth.PhoneAuthProvider
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract.ViewEvent
import com.precopia.domain.constants.PhoneNumValidationResults.SmsSent
import com.precopia.domain.constants.PhoneNumValidationResults.Validated
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PhoneReAuthLogicTest {

    private val view = mockk<IPhoneReAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IPhoneReAuthContract.ViewModel>(relaxUnitFun = true)

    private val userRepo = mockk<IRepositoryContract.UserRepository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = PhoneReAuthLogic(view, viewModel, userRepo, schedulerProvider)


    private val validPhoneNum = "1235550100"

    private val verificationId = "verificationId"

    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    @Nested
    inner class ConfirmPhoneNumber {
        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be valid.
         * - Save phone number to ViewModel.
         * - Display loading.
         * - Verify phone number via UserRepo.
         *   - It will be valid - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onCodeSent] will be called.
         * - Display message that SMS has been sent.
         * - Display SMS view with the phone number from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - valid number - sent sms`() {
            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgSmsSent } returns message
            every { userRepo.validatePhoneNumber(phoneNum = validPhoneNum) } answers { Single.just(SmsSent(verificationId)) }

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { view.displayLoading() }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { view.displayMessage(message) }
            verify { view.openSmsVerification(validPhoneNum, verificationId) }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be valid.
         * - Save phone number to ViewModel.
         * - Display loading.
         * - Verify phone number via UserRepo.
         *   - This will fail - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationFailed] will be called
         *   with [AuthInvalidCredentialsException].
         * - Throw an Exception.
         * - Hide loading.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - valid number - failed - invalid credentials`() {
            val firebaseException = mockk<AuthInvalidCredentialsException>(relaxed = true)

            every { viewModel.msgInvalidNum } returns message
            every { userRepo.validatePhoneNumber(phoneNum = validPhoneNum) } answers { Single.error(firebaseException) }

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { view.displayLoading() }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { firebaseException.printStackTrace() }
            verify { view.hideLoading() }
            verify { view.displayError(message) }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be valid.
         * - Save phone number to ViewModel.
         * - Display loading.
         * - Verify phone number via UserRepo.
         *   - This will fail - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationFailed] will be called
         *   with [AuthInvalidCredentialsException].
         * - Throw an Exception.
         * - Display a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - confirm phone num - valid number - failed - too many requests`() {
            val firebaseException = mockk<AuthTooManyRequestsException>(relaxed = true)

            every { viewModel.msgTooManyRequest } returns message
            every { userRepo.validatePhoneNumber(phoneNum = validPhoneNum) } answers { Single.error(firebaseException) }

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { view.displayLoading() }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { firebaseException.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be valid.
         * - Save phone number to ViewModel.
         * - Display loading.
         * - Verify phone number via UserRepo.
         *   - This will fail - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationFailed] will be called
         *   with a general Exception.
         * - Throw an Exception.
         * - Display an error with a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - confirm phone num - valid number - failed - general exception`() {
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.msgGenericError } returns message
            every { userRepo.validatePhoneNumber(phoneNum = validPhoneNum) } answers { Single.error(exception) }

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { view.displayLoading() }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { exception.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be valid for this test.
         * - Save phone number to ViewModel.
         * - Display loading.
         * - Verify phone number via UserRepo.
         *   - User will be instantly verified - thus
         *   [PhoneAuthProvider.OnVerificationStateChangedCallbacks.onVerificationCompleted] will be called.
         * - Thrown an Exception.
         * - Display a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - confirm phone num - valid number - verification completed`() {
            every { viewModel.msgTryAgainLater } returns message
            every { userRepo.validatePhoneNumber(phoneNum = validPhoneNum) } answers { Single.just(Validated) }

            logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { view.displayLoading() }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
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