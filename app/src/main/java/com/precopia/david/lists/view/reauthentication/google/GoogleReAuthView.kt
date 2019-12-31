package com.precopia.david.lists.view.reauthentication.google

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.precopia.david.lists.R
import com.precopia.david.lists.common.application
import com.precopia.david.lists.common.navigate
import com.precopia.david.lists.common.navigateUp
import com.precopia.david.lists.common.toast
import com.precopia.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvent
import com.precopia.david.lists.view.reauthentication.google.buildlogic.DaggerGoogleReAuthComponent
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
        navigate(GoogleReAuthViewDirections.actionGoogleReAuthViewToAuthView())
    }

    override fun finishView() {
        navigateUp()
    }


    override fun displayMessage(message: String) {
        toast(message)
    }
}
