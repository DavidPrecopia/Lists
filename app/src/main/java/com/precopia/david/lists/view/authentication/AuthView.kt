package com.precopia.david.lists.view.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.precopia.david.lists.R
import com.precopia.david.lists.common.application
import com.precopia.david.lists.view.authentication.IAuthContract.LogicEvents
import com.precopia.david.lists.view.authentication.IAuthContract.ViewEvents
import com.precopia.david.lists.view.authentication.buildlogic.DaggerAuthComponent
import kotlinx.android.synthetic.main.auth_view.*
import splitties.toast.longToast
import javax.inject.Inject
import javax.inject.Provider

class AuthView : Fragment(R.layout.auth_view), IAuthContract.View {

    @Inject
    lateinit var logic: IAuthContract.Logic

    @Inject
    lateinit var authIntent: Provider<Intent>

    @Inject
    lateinit var authUi: Provider<AuthUI>

    private val args: AuthViewArgs by navArgs()

    private var signInRequestCode: Int = 0


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListener()
        with(logic) {
            logic.onEvent(LogicEvents.OnStart(args.signOut))
            observe().observe(viewLifecycleOwner, Observer { evalViewEvents(it) })
        }
    }

    private fun inject() {
        DaggerAuthComponent.builder()
                .application(application)
                .view(this)
                .build()
                .inject(this)
    }


    private fun evalViewEvents(event: ViewEvents) {
        when (event) {
            is ViewEvents.SignIn -> signIn(event.requestCode)
            is ViewEvents.DisplayEmailSentMessage -> displayEmailSentMessage(event.email)
            ViewEvents.HideEmailSentMessage -> hideEmailSentMessage()
            is ViewEvents.DisplayMessage -> displayMessage(event.message)
            ViewEvents.OpenMainView -> openMainView()
            ViewEvents.FinishView -> finishView()
        }
    }


    private fun initClickListener() {
        check_email_button.setOnClickListener {
            logic.onEvent(LogicEvents.VerifyEmailButtonClicked)
        }
    }


    private fun signIn(requestCode: Int) {
        signInRequestCode = requestCode
        startActivityForResult(
                authIntent.get(),
                requestCode
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            signInRequestCode -> evalSignInResult(IdpResponse.fromResultIntent(data), resultCode)
        }
    }

    private fun evalSignInResult(response: IdpResponse?, resultCode: Int) {
        when {
            resultCode == Activity.RESULT_OK -> logic.onEvent(LogicEvents.SignInSuccessful)
            response === null -> logic.onEvent(LogicEvents.SignInCancelled)
            else -> logic.onEvent(LogicEvents.SignInFailed(response.error!!.errorCode))
        }
    }


    private fun displayEmailSentMessage(email: String) {
        progress_bar.visibility = View.GONE
        email_sent_body.text = getString(R.string.email_sent_message, email)
        email_sent_group.visibility = View.VISIBLE
    }

    private fun hideEmailSentMessage() {
        email_sent_group.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
    }

    private fun displayMessage(message: String) {
        longToast(message)
    }


    private fun openMainView() {
        findNavController().navigate(
                AuthViewDirections.actionAuthViewToUserListListView()
        )
    }


    private fun finishView() {
        requireActivity().finish()
    }
}
