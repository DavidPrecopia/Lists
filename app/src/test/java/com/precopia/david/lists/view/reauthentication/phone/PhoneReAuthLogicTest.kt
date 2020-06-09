package com.precopia.david.lists.view.reauthentication.phone

import androidx.lifecycle.Observer
import com.google.firebase.auth.PhoneAuthProvider
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract.ViewEvents
import com.precopia.domain.constants.PhoneNumValidationResults.SmsSent
import com.precopia.domain.constants.PhoneNumValidationResults.Validated
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [InstantExecutorExtension::class])
class PhoneReAuthLogicTest {

    private val viewModel = mockk<IPhoneReAuthContract.ViewModel>(relaxUnitFun = true)

    private val userRepo = mockk<IRepositoryContract.UserRepository>(relaxUnitFun = true)

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = PhoneReAuthLogic(viewModel, userRepo, schedulerProvider)


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
         * - [LogicEvents.ConfirmPhoneNumClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.phoneNumber } returns validPhoneNum
            every { viewModel.msgSmsSent } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.just(SmsSent(verificationId))
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.OpenSmsVerification(validPhoneNum, verificationId))

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.ConfirmPhoneNumClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val firebaseException = mockk<AuthInvalidCredentialsException>(relaxed = true)

            every { viewModel.msgInvalidNum } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.error(firebaseException)
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { firebaseException.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.HideLoading)
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.DisplayError(message))

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.ConfirmPhoneNumClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val firebaseException = mockk<AuthTooManyRequestsException>(relaxed = true)

            every { viewModel.msgTooManyRequest } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.error(firebaseException)
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { firebaseException.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.ConfirmPhoneNumClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.msgGenericError } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.error(exception)
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayError(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.ConfirmPhoneNumClicked]
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.msgTryAgainLater } returns message
            every {
                userRepo.validatePhoneNumber(phoneNum = validPhoneNum)
            } answers {
                Single.just(Validated)
            }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(validPhoneNum))

            verify { viewModel.phoneNumber = validPhoneNum }
            verify { userRepo.validatePhoneNumber(validPhoneNum) }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }


        /**
         * - [LogicEvents.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be invalid because it is empty.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - invalid number - empty`() {
            val emptyPhoneNum = ""

            every { viewModel.msgInvalidNum } returns message

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(emptyPhoneNum))

            verify { userRepo wasNot Called }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayError(message))
            }
        }

        /**
         * - [LogicEvents.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be invalid because it contains a letter.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - invalid number - contains a letter`() {
            val phoneNumWithLetter = validPhoneNum + "L"

            every { viewModel.msgInvalidNum } returns message

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(phoneNumWithLetter))

            verify { userRepo wasNot Called }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayError(message))
            }
        }

        /**
         * - [LogicEvents.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be invalid because it too short.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - invalid number - too short`() {
            val shortPhoneNum = "123"

            every { viewModel.msgInvalidNum } returns message

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(shortPhoneNum))

            verify { userRepo wasNot Called }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayError(message))
            }
        }

        /**
         * - [LogicEvents.ConfirmPhoneNumClicked]
         * - Validate the number
         *   - It will be invalid because it too long.
         * - Display an error with a message from the ViewModel.
         */
        @Test
        fun `onEvent - confirm phone num - invalid number - too long`() {
            val longPhoneNum = Long.MAX_VALUE.toString()

            every { viewModel.msgInvalidNum } returns message

            logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(longPhoneNum))

            verify { userRepo wasNot Called }

            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayError(message))
            }
        }
    }
}