package com.example.david.lists.view.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.david.lists.R
import com.example.david.lists.view.authentication.buildlogic.DaggerAuthViewComponent
import com.example.david.lists.view.common.ActivityBase
import com.example.david.lists.view.userlistlist.UserListActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.auth_view.*
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.longToast
import javax.inject.Inject
import javax.inject.Provider

/**
 * This is an Activity, instead of a Fragment, because Firebase Auth depends
 * upon [Activity.onActivityResult] and this
 * needs to return an Intent to its caller.
 */
class AuthView : ActivityBase(R.layout.auth_view), IAuthContract.View {

    @Inject
    lateinit var logic: IAuthContract.Logic

    @Inject
    lateinit var authIntent: Provider<Intent>

    @Inject
    lateinit var authUi: Provider<AuthUI>


    private var mainActivityRequestCode: Int = 0
    private var signInRequestCode: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)

        initClickListener()
        logic.onStart()
    }

    private fun inject() {
        DaggerAuthViewComponent.builder()
                .application(application)
                .activity(this)
                .view(this)
                .build()
                .inject(this)
    }

    private fun initClickListener() {
        check_email_button.setOnClickListener { logic.verifyEmail() }
    }


    override fun signIn(requestCode: Int) {
        signInRequestCode = requestCode
        startActivityForResult(
                authIntent.get(),
                requestCode
        )
    }

    override fun signOut() {
        authUi.get().signOut(applicationContext)
                .addOnSuccessListener { logic.signOutSucceeded() }
                .addOnFailureListener { logic.signOutFailed(it) }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            mainActivityRequestCode -> logic.onActivityResult(resultCode)
            signInRequestCode -> evalSignInResult(IdpResponse.fromResultIntent(data), resultCode)
        }
    }

    private fun evalSignInResult(response: IdpResponse?, resultCode: Int) {
        when {
            resultCode == Activity.RESULT_OK -> logic.signInSuccessful()
            response == null -> logic.signInCancelled()
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
        longToast(message)
    }


    override fun openMainActivity(requestCode: Int) {
        mainActivityRequestCode = requestCode
        startActivityForResult(
                intentFor<UserListActivity>(),
                requestCode
        )
    }

    override fun finishView() {
        finish()
    }
}
