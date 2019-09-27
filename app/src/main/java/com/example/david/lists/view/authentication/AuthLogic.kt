package com.example.david.lists.view.authentication

import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.authentication.IAuthContract.AuthGoal.SIGN_IN
import com.example.david.lists.view.authentication.IAuthContract.AuthGoal.SIGN_OUT
import com.example.david.lists.view.authentication.IAuthContract.AuthResult.AUTH_CANCELLED
import com.example.david.lists.view.authentication.IAuthContract.AuthResult.AUTH_SUCCESS

class AuthLogic(private val view: IAuthContract.View,
                private val viewModel: IAuthContract.ViewModel) : IAuthContract.Logic {


    override fun onStart(authGoal: IAuthContract.AuthGoal) {
        // In case the user cancels.
        view.setResult(AUTH_CANCELLED)
        evalAuthGoal(authGoal)
    }

    private fun evalAuthGoal(authGoal: IAuthContract.AuthGoal) {
        when (authGoal) {
            SIGN_IN -> signIn()
            SIGN_OUT -> signOut()
        }
    }

    private fun signIn() {
        view.signIn(viewModel.requestCode)
    }

    override fun signInSuccessful() {
        view.setResult(AUTH_SUCCESS)
        finish(viewModel.msgSignInSucceed)
    }

    override fun signInCancelled() {
        view.setResult(AUTH_CANCELLED)
        finish(viewModel.msgSignInCanceled)
    }

    override fun signInFailed(errorCode: Int) {
        val reason = viewModel.getMsgSignInError(errorCode)
        view.setResultFailed(reason)
        finish(reason)
    }

    private fun signOut() {
        view.signOut()
    }

    override fun signOutSucceeded() {
        view.setResult(AUTH_SUCCESS)
        finish(viewModel.msgSignOutSucceed)
    }

    override fun signOutFailed(e: Exception) {
        UtilExceptions.throwException(e)
        val msg = viewModel.msgSignOutFailed
        view.setResultFailed(msg)
        finish(msg)
    }


    private fun finish(displayMessage: String) {
        view.displayMessage(displayMessage)
        view.finishView()
    }
}
