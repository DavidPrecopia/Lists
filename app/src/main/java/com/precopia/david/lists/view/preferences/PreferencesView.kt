package com.precopia.david.lists.view.preferences

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.precopia.david.lists.R
import com.precopia.david.lists.common.application
import com.precopia.david.lists.common.toast
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.LogicEvents
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.ViewEvents
import com.precopia.david.lists.view.preferences.buildlogic.DaggerPreferencesComponent
import com.precopia.david.lists.view.preferences.dialogs.ConfirmAccountDeletionDialog
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class PreferencesView : PreferenceFragmentCompat(), IPreferencesViewContract.View {

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

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_view, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        logic.observe().observe(viewLifecycleOwner, Observer { evalViewEvents(it) })
    }

    private fun evalViewEvents(event: ViewEvents) {
        when (event) {
            ViewEvents.ConfirmSignOut -> confirmSignOut()
            ViewEvents.ConfirmAccountDeletion -> confirmAccountDeletion()
            ViewEvents.OpenGoogleReAuth -> openGoogleReAuth()
            ViewEvents.OpenEmailReAuth -> openEmailReAuth()
            ViewEvents.OpenPhoneReAuth -> openPhoneReAuth()
            is ViewEvents.DisplayMessage -> displayMessage(event.message)
        }
    }

    private fun init() {
//        initToolbar()
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
        setPreferenceListener(R.string.prefs_key_sign_out) {
            logic.onEvent(LogicEvents.SignOutClicked)
        }
        setPreferenceListener(R.string.prefs_key_delete_account) {
            logic.onEvent(LogicEvents.DeleteAccountClicked)
        }
    }

    private fun setPreferenceListener(key: Int, function: () -> Unit) {
        findPreference<Preference>(getString(key))?.setOnPreferenceClickListener {
            function.invoke()
            true
        }
    }


    private fun confirmSignOut() {
        findNavController().navigate(
                PreferencesViewDirections.actionPreferencesViewToConfirmSignOutDialog()
        )
    }

    private fun confirmAccountDeletion() {
        findNavController().navigate(
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
        navigate(PreferencesViewDirections.actionPreferencesViewToGoogleReAuthView())
    }

    private fun openEmailReAuth() {
        navigate(PreferencesViewDirections.actionPreferencesViewToEmailReAuthView())
    }

    private fun openPhoneReAuth() {
        navigate(PreferencesViewDirections.actionPreferencesViewToPhoneReAuthView())
    }

    /**
     * Need to pop the backstack to ensure this is the current destination.
     */
    private fun navigate(direction: NavDirections) {
        with(findNavController()) {
            popBackStack()
            navigate(direction)
        }
    }


    private fun displayMessage(message: String) {
        toast(message)
    }
}
