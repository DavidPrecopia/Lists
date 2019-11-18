package com.example.david.lists.view.authentication.emailreauth

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.authentication.emailreauth.IEmailReAuthContract.ViewEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EmailReAuthLogicTest {

    private val view = mockk<IEmailReAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IEmailReAuthContract.ViewModel>()

    private val userRepo = mockk<IRepositoryContract.UserRepository>()


    private val logic = EmailReAuthLogic(view, viewModel, userRepo)


    private val message = "message"
    private val password = "password"


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    @Nested
    inner class DeleteAccount {
        /**
         * - [ViewEvent.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Successfully delete the account via UserRepo, passing the password from the [ViewEvent].
         * - Display message from ViewModel.
         * - Open the auth view.
         */
        @Test
        fun `onEvent - Delete Account - valid password - successful`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()

            every { viewModel.msgAccountDeletionSucceed } returns message
            every {
                userRepo.deleteEmailUser(
                        password,
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.DeleteAcctClicked(password))

            captureArgSuccess.captured.onSuccess(null)

            verify {
                userRepo.deleteEmailUser(password, captureArgSuccess.captured, captureArgFailure.captured)
            }
            verify { view.displayMessage(message) }
            verify { view.openAuthView() }
        }

        /**
         * - [ViewEvent.DeleteAcctClicked].
         * - Validate the password.
         *   - It will be valid in this test.
         * - Fail to delete the account via UserRepo, passing the password from the [ViewEvent].
         * - Thrown an Exception.
         * - Display message from ViewModel.
         * - Finish the view.
         */
        @Test
        fun `onEvent - Delete Account - valid password - failed`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()
            val exception = mockk<Exception>(relaxed = true)

            every { viewModel.msgAccountDeletionFailed } returns message
            every {
                userRepo.deleteEmailUser(
                        password,
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onEvent(ViewEvent.DeleteAcctClicked(password))

            captureArgFailure.captured.onFailure(exception)

            verify {
                userRepo.deleteEmailUser(password, captureArgSuccess.captured, captureArgFailure.captured)
            }
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

            every { viewModel.invalidPassword } returns message

            logic.onEvent(ViewEvent.DeleteAcctClicked(blankPassword))

            verify { view.displayError(message) }
            verify { userRepo wasNot Called }
        }
    }
}