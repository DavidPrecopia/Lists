package com.example.david.lists.view.authentication

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class AuthLogic(private val view: IAuthContract.View,
                private val viewModel: IAuthContract.ViewModel,
                private val userRepo: IRepositoryContract.UserRepository) : IAuthContract.Logic {

    override fun onStart(signOut: Boolean) {
        when {
            signOut -> signOut()
            userRepo.userVerified -> view.openMainView()
            userRepo.signedOut -> view.signIn(viewModel.signInRequestCode)
            userRepo.hasEmail && userRepo.emailVerified.not() -> verifyEmail()
            else -> UtilExceptions.throwException(IllegalStateException())
        }
    }


    override fun signInSuccessful() {
        when (userRepo.hasEmail && userRepo.emailVerified.not()) {
            true -> verifyEmail()
            false -> {
                view.displayMessage(viewModel.msgSignInSucceed)
                view.openMainView()
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
        userRepo.signOut(
                signOutSucceeded(),
                signOutFailed()
        )
    }

    private fun signOutSucceeded() = OnSuccessListener<Void> {
        view.displayMessage(viewModel.msgSignOutSucceed)
        view.signIn(viewModel.signInRequestCode)
    }

    private fun signOutFailed() = OnFailureListener { e ->
        UtilExceptions.throwException(e)
        view.displayMessage(viewModel.msgSignOutFailed)
        view.openMainView()
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
                view.openMainView()
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
