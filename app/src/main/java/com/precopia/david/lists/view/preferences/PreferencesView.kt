package com.precopia.david.lists.view.preferences

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.precopia.david.lists.R
import com.precopia.david.lists.common.application
import com.precopia.david.lists.common.toast
import com.precopia.david.lists.util.IUtilThemeContract.ThemeLabels
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.LogicEvents
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.ViewEvents
import com.precopia.david.lists.view.preferences.buildlogic.DaggerPreferencesComponent
import com.precopia.david.lists.view.preferences.dialogs.ConfirmAccountDeletionDialog
import com.precopia.david.lists.view.preferences.dialogs.ThemeDialog
import kotlinx.android.synthetic.main.preferences_view.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class PreferencesView : Fragment(R.layout.preferences_view), IPreferencesViewContract.View {

    @Inject
    lateinit var logic: IPreferencesViewContract.Logic


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerPreferencesComponent.builder()
                .application(application)
                .view(this)
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        logic.observe().observe(viewLifecycleOwner, { evalViewEvents(it) })
    }

    /**
     * Intentionally omitting [ViewEvents.ClearLiveData].
     * See [IPreferencesViewContract] for an explanation.
     */
    private fun evalViewEvents(event: ViewEvents) {
        when (event) {
            is ViewEvents.OpenThemeSelector -> openThemeSelector(event.selectedIndex)
            ViewEvents.ConfirmSignOut -> confirmSignOut()
            ViewEvents.ConfirmAccountDeletion -> confirmAccountDeletion()
            ViewEvents.OpenGoogleReAuth -> openGoogleReAuth()
            ViewEvents.OpenEmailReAuth -> openEmailReAuth()
            ViewEvents.OpenPhoneReAuth -> openPhoneReAuth()
            is ViewEvents.DisplayMessage -> displayMessage(event.message)
        }
    }

    private fun init() {
        initToolbar()
        initClickListeners()
    }

    private fun initToolbar() {
        with(toolbar) {
            (activity as AppCompatActivity).setSupportActionBar(this)
            title = context.getString(R.string.prefs_title)
            setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun initClickListeners() {
        theme.setOnClickListener { logic.onEvent(LogicEvents.ThemeClicked) }
        sign_out.setOnClickListener { logic.onEvent(LogicEvents.SignOutClicked) }
        delete_account.setOnClickListener { logic.onEvent(LogicEvents.DeleteAccountClicked) }
    }


    private fun openThemeSelector(selectedIndex: Int) {
        navigate(
                PreferencesViewDirections.actionPreferencesViewToThemeDialog(selectedIndex, object : ThemeDialog.ThemeChangeListener {
                    override fun themeChanged(label: ThemeLabels, selectedIndex: Int) {
                        logic.onEvent(LogicEvents.ThemeChanged(label, selectedIndex))
                    }

                    override fun writeToParcel(dest: Parcel?, flags: Int) {
                    }

                    override fun describeContents() = 0
                })
        )
    }

    private fun confirmSignOut() {
        navigate(
                PreferencesViewDirections.actionPreferencesViewToConfirmSignOutDialog()
        )
    }

    private fun confirmAccountDeletion() {
        navigate(
                PreferencesViewDirections.actionPreferencesViewToConfirmAccountDeletionDialog(
                        object : ConfirmAccountDeletionDialog.DeleteAccountListener {
                            override fun deleteAccountConfirmed() {
                                logic.onEvent(LogicEvents.DeleteAccountConfirmed)
                            }

                            override fun writeToParcel(dest: Parcel?, flags: Int) {
                            }

                            override fun describeContents() = 0
                        })
        )
    }


    private fun openGoogleReAuth() {
        navigatePopBackStack(PreferencesViewDirections.actionPreferencesViewToGoogleReAuthView())
    }

    private fun openEmailReAuth() {
        navigatePopBackStack(PreferencesViewDirections.actionPreferencesViewToEmailReAuthView())
    }

    private fun openPhoneReAuth() {
        navigatePopBackStack(PreferencesViewDirections.actionPreferencesViewToPhoneReAuthView())
    }


    private fun navigate(direction: NavDirections) {
        findNavController().navigate(direction)
    }

    /**
     * Need to pop the backstack to ensure this is the current destination.
     */
    private fun navigatePopBackStack(direction: NavDirections) {
        with(findNavController()) {
            popBackStack()
            navigate(direction)
        }
    }


    private fun displayMessage(message: String) {
        toast(message)
    }
}
