package com.example.david.lists.view.reauthentication.google

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.david.lists.R
import com.example.david.lists.common.application
import com.example.david.lists.common.toast
import com.example.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvent
import com.example.david.lists.view.reauthentication.google.buildlogic.DaggerGoogleReAuthComponent
import javax.inject.Inject

class GoogleReAuthView : Fragment(R.layout.google_re_auth_view), IGoogleReAuthContract.View {

    @Inject
    lateinit var logic: IGoogleReAuthContract.Logic


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerGoogleReAuthComponent.builder()
                .application(application)
                .view(this)
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logic.onEvent(ViewEvent.OnStart)
    }


    override fun openAuthView() {
        findNavController().navigate(
                GoogleReAuthViewDirections.actionGoogleReAuthViewToAuthView()
        )
    }

    override fun finishView() {
        findNavController().navigateUp()
    }


    override fun displayMessage(message: String) {
        toast(message)
    }
}
