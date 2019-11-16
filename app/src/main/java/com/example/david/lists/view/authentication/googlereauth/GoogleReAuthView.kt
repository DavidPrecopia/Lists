package com.example.david.lists.view.authentication.googlereauth

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.david.lists.R
import com.example.david.lists.view.authentication.googlereauth.IGoogleReAuthContract.ViewEvent
import com.example.david.lists.view.authentication.googlereauth.buildlogic.DaggerGoogleReAuthComponent
import org.jetbrains.anko.toast
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
                .application(activity!!.application)
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
        context!!.toast(message)
    }
}
