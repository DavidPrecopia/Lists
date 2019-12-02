package com.example.david.lists.view.preferences

import com.example.androiddata.repository.IRepositoryContract
import com.example.david.lists.view.preferences.IPreferencesViewContract.ViewEvent
import com.example.domain.constants.AuthProviders
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class PreferencesLogicTest {

    private val view = mockk<IPreferencesViewContract.View>(relaxUnitFun = true)

    private val viewModel = mockk<IPreferencesViewContract.ViewModel>()

    private val userRepo = mockk<IRepositoryContract.UserRepository>()


    private val logic = PreferencesLogic(view, viewModel, userRepo)


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
        logic.onEvent(ViewEvent.SignOutClicked)

        verify { view.confirmSignOut() }
    }

    /**
     * Via the View, confirm the user wants to delete their account.
     */
    @Test
    fun `onEvent - Delete Account`() {
        logic.onEvent(ViewEvent.DeleteAccountClicked)

        verify { view.confirmAccountDeletion() }
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

            logic.onEvent(ViewEvent.DeleteAccountConfirmed)

            verify { view.openGoogleReAuth() }
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

            logic.onEvent(ViewEvent.DeleteAccountConfirmed)

            verify { view.openEmailReAuth() }
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

            logic.onEvent(ViewEvent.DeleteAccountConfirmed)

            verify { view.openPhoneReAuth() }
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
                logic.onEvent(ViewEvent.DeleteAccountConfirmed)
            }

            verify { view.displayMessage(message) }
        }
    }
}