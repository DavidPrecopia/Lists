package com.example.david.lists.view.preferences

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.david.lists.R
import com.example.david.lists.view.preferences.IPreferencesViewContract.ViewEvent
import com.example.david.lists.view.preferences.buildlogic.DaggerPreferencesViewComponent
import dagger.Lazy
import kotlinx.android.synthetic.main.preferences_view.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class PreferencesView : Fragment(R.layout.preferences_view), IPreferencesViewContract.View {

    @Inject
    lateinit var logic: IPreferencesViewContract.Logic

    @Inject
    lateinit var navController: Lazy<NavController>


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerPreferencesViewComponent.builder()
                .fragment(this)
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
            setNavigationOnClickListener { navController.get().navigateUp() }
        }
    }

    private fun initClickListeners() {
        sign_out.setOnClickListener { logic.onEvent(ViewEvent.SignOutClicked) }
        delete_account.setOnClickListener { logic.onEvent(ViewEvent.DeleteAccountClicked) }
    }


    override fun confirmSignOut() {
        navController.get().navigate(
                PreferencesViewDirections.actionPreferencesViewToConfirmSignOutDialog()
        )
    }

    override fun confirmAccountDeletion() {
        navController.get().navigate(
                PreferencesViewDirections.actionPreferencesViewToConfirmAccountDeletionDialog()
        )
    }
}
