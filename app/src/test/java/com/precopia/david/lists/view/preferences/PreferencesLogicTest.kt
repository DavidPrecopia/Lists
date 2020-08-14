package com.precopia.david.lists.view.preferences

import com.precopia.david.lists.InstantExecutorExtension
import com.precopia.david.lists.observeForTesting
import com.precopia.david.lists.util.IUtilThemeContract
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.LogicEvents
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.ViewEvents
import com.precopia.domain.constants.AuthProviders
import com.precopia.domain.repository.IRepositoryContract
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

    private val utilNightMode = mockk<IUtilThemeContract>(relaxUnitFun = true)


    private val logic = PreferencesLogic(viewModel, utilNightMode, userRepo)


    private val message = "message"


    @BeforeEach
    fun init() {
        clearAllMocks()
    }


    @Nested
    inner class ThemeChanged {
        /**
         * - Pass [IUtilThemeContract.ThemeValues.DAY].
         * - Invoke [IUtilThemeContract.setDay]
         */
        @Test
        fun `onEvent - ThemeChanged - day`() {
            logic.onEvent(
                    LogicEvents.ThemeChanged(IUtilThemeContract.ThemeValues.DAY.value)
            )

            verify(exactly = 1) { utilNightMode.setDay() }
        }

        /**
         * - Pass [IUtilThemeContract.ThemeValues.DARK].
         * - Invoke [IUtilThemeContract.setDark]
         */
        @Test
        fun `onEvent - ThemeChanged - dark`() {
            logic.onEvent(
                    LogicEvents.ThemeChanged(IUtilThemeContract.ThemeValues.DARK.value)
            )

            verify(exactly = 1) { utilNightMode.setDark() }
        }

        /**
         * - Pass [IUtilThemeContract.ThemeValues.FOLLOW_SYSTEM].
         * - Invoke [IUtilThemeContract.setFollowSystem]
         */
        @Test
        fun `onEvent - ThemeChanged - system`() {
            logic.onEvent(
                    LogicEvents.ThemeChanged(IUtilThemeContract.ThemeValues.FOLLOW_SYSTEM.value)
            )

            verify(exactly = 1) { utilNightMode.setFollowSystem() }
        }
    }


    @Nested
    inner class SignOut {
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
    }


    @Nested
    inner class DeleteAccount {
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