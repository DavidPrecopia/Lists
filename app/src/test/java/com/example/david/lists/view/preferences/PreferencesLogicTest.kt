package com.example.david.lists.view.preferences

import com.example.david.lists.view.preferences.IPreferencesViewContract.ViewEvent
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class PreferencesLogicTest {

    private val view = mockk<IPreferencesViewContract.View>(relaxUnitFun = true)

    private val logic = PreferencesLogic(view)


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
}