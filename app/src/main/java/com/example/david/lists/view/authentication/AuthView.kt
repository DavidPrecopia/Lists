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
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseUser
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
class AuthView : ActivityBase(), IAuthContract.View {

    @Inject
    lateinit var logic: IAuthContract.Logic

    @Inject
    lateinit var authIntent: Provider<Intent>

    @Inject
    lateinit var authUi: Provider<AuthUI>

    @Inject
    lateinit var actionCodeSettings: Provider<ActionCodeSettings>


    private var mainActivityRequestCode: Int = 0
    private var signInRequestCode: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_view)
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


    override fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification(actionCodeSettings.get())
                .addOnSuccessListener {
                    logic.sentEmailVerification()
                }.addOnFailureListener { exception ->
                    logic.failedToSendEmailVerification(exception)
                }
    }

    override fun displayEmailSentMessage(email: String) {
        progress_bar.visibility = View.GONE
        email_sent_body.text = getString(R.string.email_sent_message, email)
        email_sent_group.visibility = View.VISIBLE
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
