package com.precopia.david.lists.view.authentication

import androidx.lifecycle.Observer
import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.SchedulerProviderMockInit
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.view.authentication.IAuthContract.LogicEvents
import com.precopia.david.lists.view.authentication.IAuthContract.ViewEvents
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [InstantExecutorExtension::class])
class AuthLogicTest {

    private val viewModel = mockk<IAuthContract.ViewModel>(relaxUnitFun = true)

    private val userRepo = mockk<IRepositoryContract.UserRepository>(relaxUnitFun = true)

    private val disposable = spyk<CompositeDisposable>()

    private val schedulerProvider = mockk<ISchedulerProviderContract>()


    private val logic = AuthLogic(viewModel, userRepo, disposable, schedulerProvider)


    private val email = "email"
    private val message = "message"
    private val requestCode = 100


    @BeforeEach
    fun init() {
        clearAllMocks()
        every { viewModel.signInRequestCode } returns requestCode
        SchedulerProviderMockInit.init(schedulerProvider)
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

            logic.onEvent(LogicEvents.OnStart(signOut = false))

            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.OpenMainView)
            }
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.msgSignOutSucceed } returns message
            every { userRepo.signOut() } answers { Completable.complete() }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(signOut = true))

            verify { viewModel.emailVerificationSent = false }
            verify { userRepo.signOut() }
            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.SignIn(requestCode))

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val exception = Exception()

            every { viewModel.msgSignOutFailed } returns message
            every {
                userRepo.signOut()
            } answers { Completable.error(exception) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(signOut = true))

            verify { viewModel.emailVerificationSent = false }
            verify { userRepo.signOut() }
            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.OpenMainView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - User is not verified, and is signed-out.
         * - Sign-in via the View.
         */
        @Test
        fun `onStart - User Signed Out`() {
            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns true

            logic.onEvent(LogicEvents.OnStart(signOut = false))

            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.SignIn(requestCode))
            }
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
            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { userRepo.email } returns email
            every { viewModel.emailVerificationSent } returns false
            every { userRepo.sendVerificationEmail() } answers { Completable.complete() }

            logic.onEvent(LogicEvents.OnStart(signOut = false))

            verify { userRepo.sendVerificationEmail() }
            verify { viewModel.emailVerificationSent = true }
            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayEmailSentMessage(email))
            }
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val email = "email"
            val exception = Exception()

            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { userRepo.email } returns email
            every { viewModel.emailVerificationSent } returns false
            every { viewModel.msgSignInError } returns message
            every { userRepo.sendVerificationEmail() } answers { Completable.error(exception) }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(signOut = false))

            verify { userRepo.sendVerificationEmail() }
            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
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
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.emailVerificationSent } returns true
            every { viewModel.msgSignInSucceed } returns message
            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            // Need to change the mock's response to simulate the user being reloaded.
            every { userRepo.emailVerified } returns false andThen true
            every { userRepo.reloadUser() } answers { Completable.complete() }

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.OnStart(signOut = false))

            assertThat(listLiveDataOutput.size).isEqualTo(3)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.HideEmailSentMessage)
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[2]).isEqualTo(ViewEvents.OpenMainView)

            logic.observe().removeObserver(liveDataObserver)
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
            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { viewModel.emailVerificationSent } returns true
            every { userRepo.email } returns email
            every { viewModel.msgSignInSucceed } returns message
            every { userRepo.reloadUser() } answers { Completable.complete() }

            logic.onEvent(LogicEvents.OnStart(signOut = false))

            logic.observe().observeForTesting {
                assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayEmailSentMessage(email))
            }
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
            val exception = Exception()

            every { userRepo.userVerified } returns false
            every { userRepo.signedOut } returns false
            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns false
            every { viewModel.emailVerificationSent } returns true
            every { viewModel.msgSignInSucceed } returns message
            every { userRepo.reloadUser() } answers { Completable.error(exception) }

            logic.onEvent(LogicEvents.OnStart(signOut = false))

            logic.observe().observeForTesting {
                assertThat(logic.observe().value).isEqualTo(ViewEvents.SignIn(requestCode))
            }
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
                logic.onEvent(LogicEvents.OnStart(signOut = false))
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
                logic.onEvent(LogicEvents.OnStart(signOut = false))
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
            every { userRepo.sendVerificationEmail() } answers { Completable.complete() }

            logic.onEvent(LogicEvents.SignInSuccessful)

            verify { userRepo.sendVerificationEmail() }
        }

        /**
         * - User has email and it is verified.
         * - Display welcome message.
         * - Open the main view.
         */
        @Test
        fun `signInSuccessful - User has email and it is verified`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { userRepo.hasEmail } returns true
            every { userRepo.emailVerified } returns true
            every { viewModel.msgSignInSucceed } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.SignInSuccessful)

            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.OpenMainView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - User does not have an email.
         * - Display welcome message.
         * - Open the main view.
         */
        @Test
        fun `signInSuccessful - User does not have an email`() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { userRepo.hasEmail } returns false
            every { viewModel.msgSignInSucceed } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.SignInSuccessful)

            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.OpenMainView)

            logic.observe().removeObserver(liveDataObserver)
        }


        /***
         * - Display a message.
         * - Finish the View.
         */
        @Test
        fun signInCancelled() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }

            every { viewModel.msgSignInCanceled } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.SignInCancelled)

            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
        }

        /**
         * - Get error message from ViewModel using ErrorCode argument.
         * - Display the message.
         * - Finish the View.
         */
        @Test
        fun signInFailed() {
            val listLiveDataOutput = mutableListOf<ViewEvents>()
            val liveDataObserver = Observer<ViewEvents> { listLiveDataOutput.add(it) }
            val errorCode = 100

            every { viewModel.getMsgSignInError(errorCode) } returns message

            logic.observe().observeForever(liveDataObserver)

            logic.onEvent(LogicEvents.SignInFailed(errorCode))

            assertThat(listLiveDataOutput.size).isEqualTo(2)
            assertThat(listLiveDataOutput[0]).isEqualTo(ViewEvents.DisplayMessage(message))
            assertThat(listLiveDataOutput[1]).isEqualTo(ViewEvents.FinishView)

            logic.observe().removeObserver(liveDataObserver)
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
        every { userRepo.sendVerificationEmail() } answers { Completable.complete() }

        logic.onEvent(LogicEvents.VerifyEmailButtonClicked)

        verify { userRepo.sendVerificationEmail() }
        logic.observe().observeForTesting {
            assertThat(logic.observe().value).isEqualTo(ViewEvents.HideEmailSentMessage)
        }
    }
}