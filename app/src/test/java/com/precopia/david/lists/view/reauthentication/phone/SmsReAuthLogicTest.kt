package com.precopia.david.lists.view.reauthentication.phone

import androidx.lifecycle.Observer
import com.google.firebase.auth.PhoneAuthProvider
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.ISmsReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvents
import com.precopia.domain.constants.PhoneNumValidationResults
import com.precopia.domain.constants.PhoneNumValidationResults.Validated
import com.precopia.domain.constants.SMS_TIME_OUT_SECONDS
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [InstantExecutorExtension::class])
internal class SmsReAuthLogicTest {

    private val viewModel = mockk<ISmsReAuthContract.ViewModel>(relaxUnitFun = true)

    private val userRepo = mockk<IRepositoryContract.UserRepository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = SmsReAuthLogic(viewModel, userRepo, schedulerProvider)


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
         * - [LogicEvents.OnStart]
         * - Save the phone number and the verification ID to the ViewModel.
         * - Display a message that the SMS has been sent.
         * - Start the timer with the constant because time left is invalid.
         */
        @Test
        fun `onStart - invalid time left`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            val invalidTimeLeft = -1L

            every { viewModel.msgSmsSent } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(validPhoneNum, verificationId, invalidTimeLeft))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { viewModel.verificationId = verificationId }
            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.StartTimer(SMS_TIME_OUT_SECONDS))

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.OnStart]
         * - Save the phone number and the verification ID to the ViewModel.
         * - Start the timer with the time left value because it is valid.
         */
        @Test
        fun `onStart - valid time left`() {
            val timeLeft = 10L

            every { viewModel.msgSmsSent } returns message

            logic.onEvent(LogicEvents.OnStart(validPhoneNum, verificationId, timeLeft))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { viewModel.verificationId = verificationId }

            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.StartTimer(timeLeft))
            }
        }
    }


    @Nested
    inner class ConfirmSms {

        private val validSms = "123456"

        /**
         * - [LogicEvents.ConfirmSmsClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgAccountDeletionSucceed } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms
                )
            } answers { Completable.complete() }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmSmsClicked(validSms))

            verify { userRepo.deletePhoneUser(verificationId, validSms) }
            assertThat(listLiveDataOutput.size).isEqualTo(4)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.CancelTimer)
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[3]).isEqualTo(ViewEvents.OpenAuthView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.ConfirmSmsClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<AuthInvalidCredentialsException>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgInvalidSms } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms
                )
            } answers { Completable.error(exception) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmSmsClicked(validSms))

            verify { userRepo.deletePhoneUser(verificationId, validSms) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.HideLoading)
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.DisplayError(message))

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.ConfirmSmsClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<AuthTooManyRequestsException>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgTooManyRequest } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms
                )
            } answers { Completable.error(exception) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmSmsClicked(validSms))

            verify { userRepo.deletePhoneUser(verificationId, validSms) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.ConfirmSmsClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.verificationId } returns verificationId
            every { viewModel.msgAccountDeletionFailed } returns message
            every {
                userRepo.deletePhoneUser(
                        verificationId = verificationId,
                        smsCode = validSms
                )
            } answers { Completable.error(exception) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmSmsClicked(validSms))

            verify { userRepo.deletePhoneUser(verificationId, validSms) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.ConfirmSmsClicked]
         * - Validate the SMS number.
         *   - It will be invalid because it is empty.
         * - Display a error message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm sms - invalid sms - empty`() {
            val emptySms = ""

            every { viewModel.msgInvalidSms } returns message

            logic.onEvent(LogicEvents.ConfirmSmsClicked(emptySms))

            verify { userRepo wasNot Called }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayError(message))
            }
        }

        /**
         * - [LogicEvents.ConfirmSmsClicked]
         * - Validate the SMS number.
         *   - It will be invalid because it contains a letter.
         * - Display a error message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm sms - invalid sms - contains a letter`() {
            val smsWithLetter = validSms + "L"

            every { viewModel.msgInvalidSms } returns message

            logic.onEvent(LogicEvents.ConfirmSmsClicked(smsWithLetter))

            verify { userRepo wasNot Called }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayError(message))
            }
        }
    }


    @Nested
    inner class TimerFinished {
        /**
         * - [LogicEvents.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - This will be successful.
         * - Save the validation ID to the ViewModel.
         * - Display message that SMS has been sent.
         * - Display SMS verification.
         * - Start timer.
         */
        @Test
        fun `onEvent - timer finished - re-sent sms`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgSmsSent } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.just(PhoneNumValidationResults.SmsSent(verificationId))
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.TimerFinished)

            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { viewModel.verificationId = verificationId }
            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.StartTimer(SMS_TIME_OUT_SECONDS))

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - This will fail.
         * - Throw an Exception.
         * - Display an error with a message from the ViewModel.
         * - Cancel the Timer.
         * - Finish the View.
         */
        @Test
        fun `onEvent - timer finished - failed`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgGenericError } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.error(exception)
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.TimerFinished)

            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.CancelTimer)
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.TimerFinished]
         * - Re-send the SMS by validating the phone number via the UserRepo.
         *   - This will fail.
         * - Throw an Exception.
         * - Display an error with a message from the ViewModel.
         * - Cancel the Timer.
         * - Finish the View.
         */
        @Test
        fun `onEvent - timer finished - failed - too many requests`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<AuthTooManyRequestsException>(relaxed = true)

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgTooManyRequest } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.error(exception)
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.TimerFinished)

            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.CancelTimer)
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.TimerFinished]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgTryAgainLater } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.just(Validated)
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.TimerFinished)

            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.CancelTimer)
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }
    }


    @Nested
    inner class ViewDestroyed {
        /**
         * - [LogicEvents.ViewDestroyed]
         * - Cancel the timer.
         */
        @Test
        fun viewDestroyed() {
            logic.onEvent(LogicEvents.ViewDestroyed)

            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.CancelTimer)
            }
        }
    }
}