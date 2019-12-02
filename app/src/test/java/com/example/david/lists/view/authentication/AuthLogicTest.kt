package com.example.david.lists.view.authentication

import com.example.domain.repository.IRepositoryContract
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AuthLogicTest {

    private val view = mockk<IAuthContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IAuthContract.ViewModel>(relaxUnitFun = true)

    private val userRepo = mockk<IRepositoryContract.UserRepository>(relaxUnitFun = true)


    private val logic = AuthLogic(view, viewModel, userRepo)

    private val email = "email"
    private val message = "message"
    private val requestCode = 100


    @BeforeEach
    fun init() {
        clearAllMocks()
        every { viewModel.signInRequestCode } returns requestCode
    }


    @Nested
    inner class OnStart {
        /**
         * - User is verified.
         * - Open the main view.
         */
        @Test
        fun `onStart - User Verified`() {
            every { userRepo.userVerified } returns true

            logic.onStart(signOut = false)

            verify { view.openMainView() }
        }

        /**
         * - Sign out
         * - Set verification email sent to false.
         * - Sign-out via the UserRepo.
         * - Display message.
         * - Display sign-in.
         */
        @Test
        fun `onStart - Sign Out - succeeded`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()

            every { viewModel.msgSignOutSucceed } returns message
            every {
                userRepo.signOut(
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onStart(signOut = true)

            // Need to manually call the listener because the UserRepo is mocked.
            captureArgSuccess.captured.onSuccess(null)

            verify { viewModel.emailVerificationSent = false }
            verify { userRepo.signOut(captureArgSuccess.captured, captureArgFailure.captured) }
            verify { view.displayMessage(message) }
            verify { view.signIn(requestCode) }
        }

        /**
         * - Sign out
         * - Set verification email sent to false.
         * - Sign-out via the UserRepo.
         * - Throw an Exception.
         * - Display message.
         * - Re-open the main view.
         */
        @Test
        fun `onStart - Sign Out - failed`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()
            val exception = Exception()

            every { viewModel.msgSignOutFailed } returns message
            every {
                userRepo.signOut(
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onStart(signOut = true)

            // Need to manually call the listener because the UserRepo is mocked.
            captureArgFailure.captured.onFailure(exception)

            verify { viewModel.emailVerificationSent = false }
            verify { userRepo.signOut(captureArgSuccess.captured, captureArgFailure.captured) }
            verify { view.displayMessage(message) }
            verify { view.openMainView() }
        }

        /**
         * - User is not verified, and is signed-out.
         * - Sign-in via the View.
         */
        @Test
        fun `onStart - User Signed Out`() {
            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns true

            logic.onStart(signOut = false)

            verify { view.signIn(requestCode) }
        }

        /**
         * - The user has an email and it has not been verified.
         * - Check if the email verification has been sent.
         *   - Will be false for this test.
         * - Sent verification email.
         *   - Will be successfully sent.
         * - Set email verification sent to true in the ViewModel.
         * - Display a message that the email has been sent.
         */
        @Test
        fun `onStart - Email not verified, verification not sent, successfully sent email`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()

            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { userRepo.email } returns email
            every { viewModel.emailVerificationSent } returns false
            every {
                userRepo.sendVerificationEmail(
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onStart(signOut = false)

            // Need to manually call the listener because the UserRepo is mocked.
            captureArgSuccess.captured.onSuccess(null)

            verify { userRepo.sendVerificationEmail(any(), any()) }
            verify { viewModel.emailVerificationSent = true }
            verify { view.displayEmailSentMessage(email) }
        }

        /**
         * - The user has an email and it has not been verified.
         * - Check if the email verification has been sent.
         *   - Will be false for this test.
         * - Sent verification email.
         * - Will fail to sent.
         * - Exception will be thrown.
         *   - In this test it will be caught.
         * - Finish the View with a message from the ViewModel.
         */
        @Test
        fun `onStart - Email not verified, verification not sent, failed sent email`() {
            val email = "email"
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()
            val exception = Exception()

            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { userRepo.email } returns email
            every { viewModel.emailVerificationSent } returns false
            every { viewModel.msgSignInError } returns message
            every {
                userRepo.sendVerificationEmail(
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onStart(signOut = false)

            // Need to manually call the listener because the UserRepo is mocked.
            captureArgFailure.captured.onFailure(exception)

            verify { userRepo.sendVerificationEmail(any(), any()) }
            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - The user has an email and it has not been verified.
         * - Email verification has been sent.
         * - Reload the user.
         *   - Will be successful.
         * - Email will be verified.
         * - Hide the email sent message.
         * - Display welcome message.
         * - Open the main view.
         */
        @Test
        fun `onStart - Email not verified, verification sent, successfully reload user, email verified`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()

            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { viewModel.emailVerificationSent } returns true
            every { viewModel.msgSignInSucceed } returns message
            every {
                userRepo.reloadUser(
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onStart(signOut = false)

            // Need to change the mock's response to simulate the user being reloaded.
            every { userRepo.emailVerified } returns true

            // Need to manually call the listener because the UserRepo is mocked.
            captureArgSuccess.captured.onSuccess(null)

            verify { view.hideEmailSentMessage() }
            verify { view.displayMessage(message) }
            verify { view.openMainView() }
        }

        /**
         * - The user has an email and it has not been verified.
         * - Email verification has been sent.
         * - Reload the user.
         *   - Will be successful.
         * - Email will still not be verified.
         * - Re-display the email sent message.
         */
        @Test
        fun `onStart - Email not verified, verification sent, successfully reload user, email still not verified`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()

            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { viewModel.emailVerificationSent } returns true
            every { userRepo.email } returns email
            every { viewModel.msgSignInSucceed } returns message
            every {
                userRepo.reloadUser(
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onStart(signOut = false)

            // Need to manually call the listener because the UserRepo is mocked.
            captureArgSuccess.captured.onSuccess(null)

            verify { view.displayEmailSentMessage(email) }
        }

        /**
         * - The user has an email and it has not been verified.
         * - Email verification has been sent.
         * - Reload the user.
         *   - Will fail.
         * - Exception is thrown.
         * - Display sign-in.
         */
        @Test
        fun `onStart - Email not verified, verification sent, failed to reload user`() {
            val captureArgSuccess = CapturingSlot<OnSuccessListener<in Void>>()
            val captureArgFailure = CapturingSlot<OnFailureListener>()
            val exception = Exception()

            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { viewModel.emailVerificationSent } returns true
            every { viewModel.msgSignInSucceed } returns message
            every {
                userRepo.reloadUser(
                        successListener = capture(captureArgSuccess),
                        failureListener = capture(captureArgFailure)
                )
            } answers { Unit }

            logic.onStart(signOut = false)

            // Need to manually call the listener because the UserRepo is mocked.
            captureArgFailure.captured.onFailure(exception)

            verify { view.signIn(requestCode) }
        }

        /**
         * - The user is not verified, is not signed-out, and has an email and email has been verified.
         * - Exception is thrown.
         */
        @Test
        fun `onStart - Invalid State`() {
            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns true

            assertThrows<IllegalStateException> {
                logic.onStart(signOut = false)
            }
        }

        /**
         * - The user is not verified, is not signed-out, and has does not have an email.
         * - Exception is thrown.
         */
        @Test
        fun `onStart - Invalid State No Email`() {
            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns false

            assertThrows<IllegalStateException> {
                logic.onStart(signOut = false)
            }
        }
    }


    @Nested
    inner class SignIn {
        /**
         * - User has an email, but it is not verified.
         * - Check if the verification email has been sent.
         *   - Has not been sent in this test.
         * - Sent the verification email.
         *
         * I am only verifying that the email is sent because the logic
         * beyond that is tested in another test.
         */
        @Test
        fun `signInSuccessful - User has email and is not verified`() {
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { viewModel.emailVerificationSent } returns false

            logic.signInSuccessful()

            verify { userRepo.sendVerificationEmail(any(), any()) }
        }

        /**
         * - User has email and it is verified.
         * - Display welcome message.
         * - Open the main view.
         */
        @Test
        fun `signInSuccessful - User has email and it is verified`() {
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns true
            every { viewModel.msgSignInSucceed } returns message

            logic.signInSuccessful()

            verify { view.displayMessage(message) }
            verify { view.openMainView() }
        }

        /**
         * - User does not have an email.
         * - Display welcome message.
         * - Open the main view.
         */
        @Test
        fun `signInSuccessful - User does not have an email`() {
            every { userRepo.hasEmail } returns false
            every { viewModel.msgSignInSucceed } returns message

            logic.signInSuccessful()

            verify { view.displayMessage(message) }
            verify { view.openMainView() }
        }


        /***
         * - Display a message.
         * - Finish the View.
         */
        @Test
        fun signInCancelled() {
            every { viewModel.msgSignInCanceled } returns message

            logic.signInCancelled()

            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }

        /**
         * - Get error message from ViewModel using ErrorCode argument.
         * - Display the message.
         * - Finish the View.
         */
        @Test
        fun signInFailed() {
            val errorCode = 100

            every { viewModel.getMsgSignInError(errorCode) } returns message

            logic.signInFailed(errorCode)

            verify { view.displayMessage(message) }
            verify { view.finishView() }
        }
    }

    /**
     * - Hide email sent message.
     * - Check if the verification email has been sent.
     *   - Has not been sent in this test.
     * - Sent the verification email.
     *
     * I am only verifying that the email is sent because the logic
     * beyond that is tested in another test.
     */
    @Test
    fun verifyEmailButtonClicked() {
        every { viewModel.emailVerificationSent } returns false

        logic.verifyEmailButtonClicked()

        verify { view.hideEmailSentMessage() }
        verify { userRepo.sendVerificationEmail(any(), any()) }
    }
}