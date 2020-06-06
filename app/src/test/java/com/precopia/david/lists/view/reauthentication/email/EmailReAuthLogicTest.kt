package com.precopia.david.lists.view.reauthentication.email

import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract.ViewEvent
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EmailReAuthLogicTest {

    private val view = mockk<IEmailReAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IEmailReAuthContract.ViewModel>()

    private val userRepo = mockk<IRepositoryContract.UserRepository>()

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = EmailReAuthLogic(view, viewModel, userRepo, schedulerProvider)


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
         * - [ViewEvent.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Display loading.
         * - Successfully delete the account via UserRepo, passing the password from the [ViewEvent].
         * - Display message from ViewModel.
         * - Open the auth view.
         */
        @Test
        fun `onEvent - Delete Account - valid password - successful`() {
            every { viewModel.msgAccountDeletionSucceed } returns message
            every { userRepo.deleteEmailUser(password) } answers { Completable.complete() }

            logic.onEvent(ViewEvent.DeleteAcctClicked(password))

            verify { view.displayLoading() }
            verify { userRepo.deleteEmailUser(password) }
            verify { view.displayMessage(message) }
            verify { view.openAuthView() }
        }

        /**
         * - [ViewEvent.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Display loading.
         * - Fail to delete the account via UserRepo, passing the password from the [ViewEvent].
         * - Thrown an Exception.
         *   - Specifically, [AuthInvalidCredentialsException]
         * - Hide loading.
         * - Display error message from ViewModel.
         */
        @Test
        fun `onEvent - Delete Account - valid password - failed - invalid credentials`() {
            val exception = mockk<AuthInvalidCredentialsException>(relaxed = true)

            every { viewModel.msgInvalidPassword } returns message
            every { userRepo.deleteEmailUser(password) } answers { Completable.error(exception) }

            logic.onEvent(ViewEvent.DeleteAcctClicked(password))

            verify { view.displayLoading() }
            verify { userRepo.deleteEmailUser(password) }
            verify { exception.printStackTrace() }
            verify { view.hideLoading() }
            verify { view.displayError(message) }
        }

        /**
         * - [ViewEvent.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Display loading.
         * - Fail to delete the account via UserRepo, passing the password from the [ViewEvent].
         * - Thrown an Exception.
         *   - Specifically, [AuthTooManyRequestsException]
         * - Display error message from ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - Delete Account - valid password - failed - too many requests`() {
            val exception = mockk<AuthTooManyRequestsException>(relaxed = true)

            every { viewModel.msgTooManyRequest } returns message
            every { userRepo.deleteEmailUser(password) } answers { Completable.error(exception) }

            logic.onEvent(ViewEvent.DeleteAcctClicked(password))

            verify { view.displayLoading() }
            verify { userRepo.deleteEmailUser(password) }
            verify { exception.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Display loading.
         * - Fail to delete the account via UserRepo, passing the password from the [ViewEvent].
         * - Thrown an Exception.
         *   - Specifically, [Exception]
         * - Display error message from ViewModel.
         * - Finish the View.
         */
        @Test
        fun `onEvent - Delete Account - valid password - failed - general exception`() {
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.msgAccountDeletionFailed } returns message
            every { userRepo.deleteEmailUser(password) } answers { Completable.error(exception) }

            logic.onEvent(ViewEvent.DeleteAcctClicked(password))

            verify { view.displayLoading() }
            verify { userRepo.deleteEmailUser(password) }
            verify { exception.printStackTrace() }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - [ViewEvent.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be invalid for this test.
         * - Display an error from the ViewModel.
         */
        @Test
        fun `onEvent - Delete Account - invalid password - blank`() {
            val blankPassword = ""

            every { viewModel.msgInvalidPassword } returns message

            logic.onEvent(ViewEvent.DeleteAcctClicked(blankPassword))

            verify { view.displayError(message) }
            verify { userRepo wasNot Called }
        }
    }
}