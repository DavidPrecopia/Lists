package com.precopia.david.lists.view.reauthentication.email

import androidx.lifecycle.Observer
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract.ViewEvents
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [InstantExecutorExtension::class])
class EmailReAuthLogicTest {

    private val viewModel = mockk<IEmailReAuthContract.ViewModel>()

    private val userRepo = mockk<IRepositoryContract.UserRepository>()

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = EmailReAuthLogic(viewModel, userRepo, schedulerProvider)


    private val message = "message"
    private val password = "password"


    @BeforeEach
    fun init() {
        clearAllMocks()
        SchedulerProviderMockInit.init(schedulerProvider)
    }


    @Nested
    inner class DeleteAccount {
        /**
         * - [LogicEvents.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Display loading.
         * - Successfully delete the account via UserRepo, passing the password from the [LogicEvents].
         * - Display message from ViewModel.
         * - Open the auth view.
         */
        @Test
        fun `onEvent - Delete Account - valid password - successful`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.msgAccountDeletionSucceed } returns message
            every { userRepo.deleteEmailUser(password) } answers { Completable.complete() }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.DeleteAcctClicked(password))

            verify { userRepo.deleteEmailUser(password) }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.OpenAuthView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Display loading.
         * - Fail to delete the account via UserRepo, passing the password from the [LogicEvents].
         * - Thrown an Exception.
         *   - Specifically, [AuthInvalidCredentialsException]
         * - Hide loading.
         * - Display error message from ViewModel.
         */
        @Test
        fun `onEvent - Delete Account - valid password - failed - invalid credentials`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<AuthInvalidCredentialsException>(relaxed = true)

            every { viewModel.msgInvalidPassword } returns message
            every { userRepo.deleteEmailUser(password) } answers { Completable.error(exception) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.DeleteAcctClicked(password))

            verify { userRepo.deleteEmailUser(password) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.HideLoading)
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.DisplayError(message))

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Display loading.
         * - Fail to delete the account via UserRepo, passing the password from the [LogicEvents].
         * - Thrown an Exception.
         *   - Specifically, [AuthTooManyRequestsException]
         * - Display error message from ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - Delete Account - valid password - failed - too many requests`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<AuthTooManyRequestsException>(relaxed = true)

            every { viewModel.msgTooManyRequest } returns message
            every { userRepo.deleteEmailUser(password) } answers { Completable.error(exception) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.DeleteAcctClicked(password))

            verify { userRepo.deleteEmailUser(password) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Display loading.
         * - Fail to delete the account via UserRepo, passing the password from the [LogicEvents].
         * - Thrown an Exception.
         *   - Specifically, [Exception]
         * - Display error message from ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - Delete Account - valid password - failed - general exception`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.msgAccountDeletionFailed } returns message
            every { userRepo.deleteEmailUser(password) } answers { Completable.error(exception) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.DeleteAcctClicked(password))

            verify { userRepo.deleteEmailUser(password) }
            verify { exception.printStackTrace() }
            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayLoading)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - [LogicEvents.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be invalid for this test.
         * - Display an error from the ViewModel.
         */
        @Test
        fun `onEvent - Delete Account - invalid password - blank`() {
            val blankPassword = ""

            every { viewModel.msgInvalidPassword } returns message

            logic.onEvent(LogicEvents.DeleteAcctClicked(blankPassword))


            verify { userRepo wasNot Called }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayError(message))
            }
        }
    }
}