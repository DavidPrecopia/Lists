package com.precopia.david.lists.view.preferences

import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.LogicEvents
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.ViewEvents
import com.precopia.domain.constants.AuthProviders
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(value = [InstantExecutorExtension::class])
class PreferencesLogicTest {

    private val viewModel = mockk<IPreferencesViewContract.ViewModel>()

    private val userRepo = mockk<IRepositoryContract.UserRepository>()


    private val logic = PreferencesLogic(viewModel, userRepo)


    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    /**
     * Via the View, confirm the user wants to sign-out.
     */
    @Test
    fun `onEvent - SignOut`() {
        logic.onEvent(LogicEvents.SignOutClicked)

        logic.observe().observeForTesting {
            Assertions.assertThat(logic.observe().value)
                    .isEqualTo(ViewEvents.ConfirmSignOut)
        }
    }

    /**
     * Via the View, confirm the user wants to delete their account.
     */
    @Test
    fun `onEvent - Delete Account`() {
        logic.onEvent(LogicEvents.DeleteAccountClicked)

        logic.observe().observeForTesting {
            Assertions.assertThat(logic.observe().value)
                    .isEqualTo(ViewEvents.ConfirmAccountDeletion)
        }
    }


    @Nested
    inner class DeleteAccountConfirmed {
        /**
         * - Delete Google user.
         * - Specify the specific provider via the UserRepo.
         *   - It will be [AuthProviders.GOOGLE] for this test.
         * - Open Google re-authentication.
         */
        @Test
        fun `deleteAccountConfirmed - Google provider`() {
            every { userRepo.authProvider } returns AuthProviders.GOOGLE

            logic.onEvent(LogicEvents.DeleteAccountConfirmed)

            logic.observe().observeForTesting {
                Assertions.assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.OpenGoogleReAuth)
            }
        }


        /**
         * - Delete Email user.
         * - Specify the specific provider via the UserRepo.
         *   - It will be [AuthProviders.EMAIL] for this test.
         * - Open email re-authentication.
         */
        @Test
        fun `deleteAccountConfirmed - Email provider`() {
            every { userRepo.authProvider } returns AuthProviders.EMAIL

            logic.onEvent(LogicEvents.DeleteAccountConfirmed)

            logic.observe().observeForTesting {
                Assertions.assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.OpenEmailReAuth)
            }
        }

        /**
         * - Delete Phone user.
         * - Specify the specific provider via the UserRepo.
         *   - It will be [AuthProviders.PHONE] for this test.
         * - Open phone re-authentication.
         */
        @Test
        fun `deleteAccountConfirmed - Phone provider`() {
            every { userRepo.authProvider } returns AuthProviders.PHONE

            logic.onEvent(LogicEvents.DeleteAccountConfirmed)

            logic.observe().observeForTesting {
                Assertions.assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.OpenPhoneReAuth)
            }
        }

        /**
         * - Delete user.
         * - Specify the specific provider via the UserRepo.
         *   - It will be [AuthProviders.UNKNOWN] for this test.
         * - Throw an Exception.
         * - Display failure message from ViewModel.
         */
        @Test
        fun `deleteAccountConfirmed - Unknown provider`() {
            every { userRepo.authProvider } returns AuthProviders.UNKNOWN
            every { viewModel.msgDeletionFailed } returns message

            assertThrows<IllegalStateException> {
                logic.onEvent(LogicEvents.DeleteAccountConfirmed)
            }

            logic.observe().observeForTesting {
                Assertions.assertThat(logic.observe().value)
                        .isEqualTo(ViewEvents.DisplayMessage(message))
            }
        }
    }
}