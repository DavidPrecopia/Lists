package com.example.david.lists.view.authentication

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class AuthLogic(private val view: IAuthContract.View,
                private val viewModel: IAuthContract.ViewModel,
                private val userRepo: IRepositoryContract.UserRepository) : IAuthContract.Logic {

    override fun onStart() {
        when {
            userRepo.userVerified -> view.openMainActivity(viewModel.mainActivityRequestCode)
            userRepo.signedOut -> view.signIn(viewModel.signInRequestCode)
            userRepo.hasEmail && userRepo.emailVerified.not() -> verifyEmail()
            else -> UtilExceptions.throwException(IllegalStateException())
        }
    }

    override fun onActivityResult(resultCode: Int) {
        when (resultCode) {
            IAuthContract.FINISH -> view.finishView()
            IAuthContract.SIGN_OUT -> signOut()
        }
    }


    override fun signInSuccessful() {
        when (userRepo.hasEmail && userRepo.emailVerified.not()) {
            true -> verifyEmail()
            false -> {
                view.displayMessage(viewModel.msgSignInSucceed)
                view.openMainActivity(viewModel.mainActivityRequestCode)
            }
        }
    }

    override fun signInCancelled() {
        finish(viewModel.msgSignInCanceled)
    }

    override fun signInFailed(errorCode: Int) {
        finish(viewModel.getMsgSignInError(errorCode))
    }


    private fun signOut() {
        // Need to re-set state in case the user
        // signs-in with an unverified email.
        viewModel.emailVerificationSent = false
        view.signOut()
    }

    override fun signOutSucceeded() {
        view.displayMessage(viewModel.msgSignOutSucceed)
        view.signIn(viewModel.signInRequestCode)
    }

    override fun signOutFailed(e: Exception) {
        UtilExceptions.throwException(e)
        view.displayMessage(viewModel.msgSignOutFailed)
        view.openMainActivity(viewModel.mainActivityRequestCode)
    }


    override fun verifyEmailButtonClicked() {
        view.hideEmailSentMessage()
        verifyEmail()
    }


    private fun verifyEmail() {
        when (viewModel.emailVerificationSent) {
            true -> userRepo.reloadUser(
                    successfullyReloadedUser(),
                    failedToReloadUser()
            )
            false -> userRepo.sendVerificationEmail(
                    successfullySentEmail(),
                    failedToSendEmail()
            )
        }
    }

    private fun successfullyReloadedUser() = OnSuccessListener<Void> {
        when (userRepo.emailVerified) {
            true -> {
                view.hideEmailSentMessage()
                view.displayMessage(viewModel.msgSignInSucceed)
                view.openMainActivity(viewModel.mainActivityRequestCode)
            }
            false -> view.displayEmailSentMessage(userRepo.email!!)
        }
    }

    private fun failedToReloadUser() = OnFailureListener { e ->
        UtilExceptions.throwException(e)
        view.signIn(viewModel.signInRequestCode)
    }

    private fun successfullySentEmail() = OnSuccessListener<Void> {
        viewModel.emailVerificationSent = true
        view.displayEmailSentMessage(userRepo.email!!)
    }

    private fun failedToSendEmail() = OnFailureListener { e ->
        UtilExceptions.throwException(e)
        finish(viewModel.msgSignInError)
    }


    private fun finish(displayMessage: String) {
        view.displayMessage(displayMessage)
        view.finishView()
    }
}
