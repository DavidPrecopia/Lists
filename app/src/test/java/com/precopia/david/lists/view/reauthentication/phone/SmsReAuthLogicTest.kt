package com.precopia.david.lists.view.reauthentication.phone

import com.google.firebase.auth.PhoneAuthProvider
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvent
import com.precopia.domain.constants.PhoneNumValidationResults
import com.precopia.domain.constants.PhoneNumValidationResults.Validated
import com.precopia.domain.constants.SMS_TIME_OUT_SECONDS
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class SmsReAuthLogicTest {

    private val view = mockk<ISmsReAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<ISmsReAuthContract.ViewModel>(relaxUnitFun = true)

    private val userRepo = mockk<IRepositoryContract.UserRepository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = SmsReAuthLogic(view, viewModel, userRepo, schedulerProvider)


    private val validPhoneNum = "1235550100"

    private val verificationId = "verificationId"

    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    @Nested
    inner class OnStart {
        /**
         * - [ViewEvent.OnStart]
         * - Save the phone number and the verification ID to the ViewModel.
         * - Display a message that the SMS has been sent.
         * - Start the timer with the constant because time left is invalid.
         */
        @Test
        fun `onStart - invalid time left`() {
            val invalidTimeLeft = -1L

            every { viewModel.msgSmsSent } returns message

            logic.onEvent(ViewEvent.OnStart(validPhoneNum, verificationId, invalidTimeLeft))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { viewModel.verificationId = verificationId }
            verify { view.displayMessage(message) }
            verify { view.startTimer(SMS_TIME_OUT_SECONDS) }
        }

        /**
         * - [ViewEvent.OnStart]
         * - Save the phone number and the verification ID to the ViewModel.
         * - Start the timer with the time left value because it is valid.
         */
        @Test
        fun `onStart - valid time left`() {
            val timeLeft = 10L

            every { viewModel.msgSmsSent } returns message

            logic.onEvent(ViewEvent.OnStart(validPhoneNum, verificationId, timeLeft))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { viewModel.verificationId = verificationId }
            verify { view.startTimer(timeLeft) }
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
            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgAccountDeletionSucceed } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms
                )
            } answers { Completable.complete() }

            logic.onEvent(ViewEvent.ConfirmSmsClicked(validSms))

            verify { view.displayLoading() }
            verify { userRepo.deletePhoneUser(verificationId, validSms) }
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
         *   - It will fail with [AuthInvalidCredentialsException].
         * - Thrown an Exception.
         * - Hide loading.
         * - Display a error message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm sms - valid sms - failure - invalid credentials`() {
            val exception = mockk<AuthInvalidCredentialsException>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgInvalidSms } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms
                )
            } answers { Completable.error(exception) }

            logic.onEvent(ViewEvent.ConfirmSmsClicked(validSms))

            verify { view.displayLoading() }
            verify { userRepo.deletePhoneUser(verificationId, validSms) }
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
         *   - It will fail with [AuthTooManyRequestsException].
         * - Thrown an Exception.
         * - Display a message from the ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - confirm sms - valid sms - failure - too many requests`() {
            val exception = mockk<AuthTooManyRequestsException>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgTooManyRequest } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms
                )
            } answers { Completable.error(exception) }

            logic.onEvent(ViewEvent.ConfirmSmsClicked(validSms))

            verify { view.displayLoading() }
            verify { userRepo.deletePhoneUser(verificationId, validSms) }
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
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgAccountDeletionFailed } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms
                )
            } answers { Completable.error(exception) }

            logic.onEvent(ViewEvent.ConfirmSmsClicked(validSms))

            verify { view.displayLoading() }
            verify { userRepo.deletePhoneUser(verificationId, validSms) }
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
            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgSmsSent } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.just(PhoneNumValidationResults.SmsSent(verificationId))
            }

            logic.onEvent(ViewEvent.TimerFinished)

            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { viewModel.verificationId = verificationId }
            verify { view.displayMessage(message) }
            verify { view.startTimer(SMS_TIME_OUT_SECONDS) }
        }

        /**
         * - [ViewEvent.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - This will fail.
         * - Throw an Exception.
         * - Display an error with a message from the ViewModel.
         * - Cancel the Timer.
         * - Finish the View.
         */
        @Test
        fun `onEvent - timer finished - failed`() {
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgGenericError } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.error<PhoneNumValidationResults>(exception)
            }

            logic.onEvent(ViewEvent.TimerFinished)

            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { exception.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.cancelTimer() }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - This will fail.
         * - Throw an Exception.
         * - Display an error with a message from the ViewModel.
         * - Cancel the Timer.
         * - Finish the View.
         */
        @Test
        fun `onEvent - timer finished - failed - too many requests`() {
            val exception = mockk<AuthTooManyRequestsException>(relaxed = true)

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgTooManyRequest } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.error<PhoneNumValidationResults>(exception)
            }

            logic.onEvent(ViewEvent.TimerFinished)

            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { exception.printStackTrace() }
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
            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgTryAgainLater } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.just(Validated)
            }

            logic.onEvent(ViewEvent.TimerFinished)

            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { view.displayMessage(message) }
            verify { view.cancelTimer() }
            verify { view.finishView() }
        }
    }


    @Nested
    inner class ViewDestroyed {
        /**
         * - [ViewEvent.ViewDestroyed]
         * - Cancel the timer.
         */
        @Test
        fun viewDestroyed() {
            logic.onEvent(ViewEvent.ViewDestroyed)

            verify { view.cancelTimer() }
        }
    }
}