package com.precopia.david.lists.view.authentication

import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.domain.repository.IRepositoryContract

class AuthLogic(private val view: IAuthContract.View,
                private val viewModel: IAuthContract.ViewModel,
                private val userRepo: IRepositoryContract.UserRepository,
                private val schedulerProvider: ISchedulerProviderContract) : IAuthContract.Logic {

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
        subscribeCompletable(
                userRepo.signOut(),
                { signOutSucceeded() },
                { signOutFailed(it) },
                schedulerProvider
        )
    }

    private fun signOutSucceeded() {
        view.displayMessage(viewModel.msgSignOutSucceed)
        view.signIn(viewModel.signInRequestCode)
    }

    private fun signOutFailed(e: Throwable) {
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
            true -> subscribeCompletable(
                    userRepo.reloadUser(),
                    { successfullyReloadedUser() },
                    { failedToReloadUser(it) },
                    schedulerProvider
            )
            false -> subscribeCompletable(
                    userRepo.sendVerificationEmail(),
                    { successfullySentEmail() },
                    { failedToSendEmail(it) },
                    schedulerProvider
            )
        }
    }

    private fun successfullyReloadedUser() {
        when (userRepo.emailVerified) {
            true -> {
                view.hideEmailSentMessage()
                view.displayMessage(viewModel.msgSignInSucceed)
                view.openMainView()
            }
            false -> view.displayEmailSentMessage(userRepo.email!!)
        }
    }

    private fun failedToReloadUser(e: Throwable) {
        UtilExceptions.throwException(e)
        view.signIn(viewModel.signInRequestCode)
    }

    private fun successfullySentEmail() {
        viewModel.emailVerificationSent = true
        view.displayEmailSentMessage(userRepo.email!!)
    }

    private fun failedToSendEmail(e: Throwable) {
        UtilExceptions.throwException(e)
        finish(viewModel.msgSignInError)
    }


    private fun finish(displayMessage: String) {
        view.displayMessage(displayMessage)
        view.finishView()
    }
}
