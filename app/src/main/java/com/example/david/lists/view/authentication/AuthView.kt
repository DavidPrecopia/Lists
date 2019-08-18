package com.example.david.lists.view.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.david.lists.R
import com.example.david.lists.view.authentication.IAuthContract.AuthResult
import com.example.david.lists.view.authentication.buildlogic.DaggerAuthViewComponent
import com.example.david.lists.view.common.ActivityBase
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import org.jetbrains.anko.toast
import javax.inject.Inject
import javax.inject.Provider

/**
 * The result code will always be [Activity.RESULT_OK],
 * see the Intent's extras for more information - see [AuthResult].
 *
 *
 * If authentication failed, the returned Intent will contain the reason why.
 *
 *
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
    lateinit var authUi: AuthUI

    private var authRequestCode: Int = 0

    private val authGoal: IAuthContract.AuthGoal
        get() = intent
                .extras!!
                .getSerializable(getString(R.string.intent_extra_auth)) as IAuthContract.AuthGoal


    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.auth_view)
        logic.onStart(authGoal)
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
        this.authRequestCode = requestCode
        startActivityForResult(
                authIntent.get(),
                requestCode
        )
    }

    override fun signOut() {
        authUi.signOut(applicationContext)
                .addOnSuccessListener { logic.signOutSucceeded() }
                .addOnFailureListener { logic.signOutFailed(it) }
    }


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == authRequestCode) {
            evalSignInResult(IdpResponse.fromResultIntent(data), resultCode)
        }
    }

    private fun evalSignInResult(response: IdpResponse?, resultCode: Int) {
        when {
            resultCode == Activity.RESULT_OK -> logic.signInSuccessful()
            response == null -> logic.signInCancelled()
            else -> logic.signInFailed(response.error!!.errorCode)
        }
    }


    override fun displayMessage(message: String) {
        toast(message)
    }


    override fun setResult(result: AuthResult) {
        setResult(Activity.RESULT_OK, getResultIntent(result))
    }

    override fun setResultFailed(reason: String) {
        val intent = getResultIntent(AuthResult.AUTH_FAILED).apply {
            putExtra(getString(R.string.intent_extra_auth_failure_reason), reason)
        }
        setResult(Activity.RESULT_OK, intent)
    }

    private fun getResultIntent(result: AuthResult) = Intent().apply {
        putExtra(getString(R.string.intent_extra_auth_result), result)
    }


    override fun finishView() {
        finish()
    }
}
