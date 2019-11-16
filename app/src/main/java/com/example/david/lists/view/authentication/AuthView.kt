package com.example.david.lists.view.authentication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.david.lists.R
import com.example.david.lists.view.authentication.buildlogic.DaggerAuthViewComponent
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.auth_view.*
import org.jetbrains.anko.longToast
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
        logic.onStart(args.signOut)
    }

    private fun inject() {
        DaggerAuthViewComponent.builder()
                .application(activity!!.application)
                .view(this)
                .build()
                .inject(this)
    }

    private fun initClickListener() {
        check_email_button.setOnClickListener { logic.verifyEmailButtonClicked() }
    }


    override fun signIn(requestCode: Int) {
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
            resultCode == Activity.RESULT_OK -> logic.signInSuccessful()
            response === null -> logic.signInCancelled()
            else -> logic.signInFailed(response.error!!.errorCode)
        }
    }


    override fun displayEmailSentMessage(email: String) {
        progress_bar.visibility = View.GONE
        email_sent_body.text = getString(R.string.email_sent_message, email)
        email_sent_group.visibility = View.VISIBLE
    }

    override fun hideEmailSentMessage() {
        email_sent_group.visibility = View.GONE
        progress_bar.visibility = View.VISIBLE
    }

    override fun displayMessage(message: String) {
        context!!.longToast(message)
    }


    override fun openMainView() {
        findNavController().navigate(
                AuthViewDirections.actionAuthViewToUserListListView()
        )
    }


    override fun finishView() {
        activity!!.finish()
    }
}
