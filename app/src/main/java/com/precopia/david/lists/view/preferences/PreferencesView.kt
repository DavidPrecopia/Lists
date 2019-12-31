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
import com.precopia.david.lists.view.preferences.IPreferencesViewContract.ViewEvent
import com.precopia.david.lists.view.preferences.buildlogic.DaggerPreferencesViewComponent
import com.precopia.david.lists.view.preferences.dialogs.ConfirmAccountDeletionDialog
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
        DaggerPreferencesViewComponent.builder()
                .application(application)
                .view(this)
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        initToolbar()
        initClickListeners()
    }

    private fun initToolbar() {
        with(toolbar) {
            (activity as AppCompatActivity).setSupportActionBar(this)
            title = context.getString(R.string.preferences_title)
            setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun initClickListeners() {
        sign_out.setOnClickListener { logic.onEvent(ViewEvent.SignOutClicked) }
        delete_account.setOnClickListener { logic.onEvent(ViewEvent.DeleteAccountClicked) }
    }


    override fun confirmSignOut() {
        findNavController().navigate(
                PreferencesViewDirections.actionPreferencesViewToConfirmSignOutDialog()
        )
    }

    override fun confirmAccountDeletion() {
        findNavController().navigate(
                PreferencesViewDirections.actionPreferencesViewToConfirmAccountDeletionDialog(
                        object : ConfirmAccountDeletionDialog.DeleteAccountListener {
                            override fun deleteAccountConfirmed() {
                                logic.onEvent(ViewEvent.DeleteAccountConfirmed)
                            }

                            override fun writeToParcel(dest: Parcel?, flags: Int) {
                            }

                            override fun describeContents() = 0
                        })
        )
    }


    override fun openGoogleReAuth() {
        navigate(PreferencesViewDirections.actionPreferencesViewToGoogleReAuthView())
    }

    override fun openEmailReAuth() {
        navigate(PreferencesViewDirections.actionPreferencesViewToEmailReAuthView())
    }

    override fun openPhoneReAuth() {
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


    override fun displayMessage(message: String) {
        toast(message)
    }
}
