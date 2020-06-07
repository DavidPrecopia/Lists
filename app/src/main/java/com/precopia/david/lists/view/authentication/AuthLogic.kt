package com.precopia.david.lists.view.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.authentication.IAuthContract.LogicEvents
import com.precopia.david.lists.view.authentication.IAuthContract.ViewEvents
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.rxjava3.disposables.CompositeDisposable

class AuthLogic(private val viewModel: IAuthContract.ViewModel,
                private val userRepo: IRepositoryContract.UserRepository,
                private val disposable: CompositeDisposable,
                private val schedulerProvider: ISchedulerProviderContract) :
        ViewModel(),
        IAuthContract.Logic {

    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(event: LogicEvents) {
        when (event) {
            is LogicEvents.OnStart -> onStart(event.signOut)
            LogicEvents.SignInSuccessful -> signInSuccessful()
            LogicEvents.SignInCancelled -> signInCancelled()
            is LogicEvents.SignInFailed -> signInFailed(event.errorCode)
            LogicEvents.VerifyEmailButtonClicked -> verifyEmailButtonClicked()
        }
    }


    override fun observe(): LiveData<ViewEvents> = viewEventLiveData


    private fun onStart(signOut: Boolean) {
        when {
            signOut -> signOut()
            userRepo.userVerified -> viewEventLiveData.value =
                    ViewEvents.OpenMainView
            userRepo.signedOut -> viewEventLiveData.value =
                    ViewEvents.SignIn(viewModel.signInRequestCode)
            userRepo.hasEmail && userRepo.emailVerified.not() -> verifyEmail()
            else -> UtilExceptions.throwException(IllegalStateException())
        }
    }

    private fun signInSuccessful() {
        when (userRepo.hasEmail && userRepo.emailVerified.not()) {
            true -> verifyEmail()
            false -> with(viewEventLiveData) {
                value = ViewEvents.DisplayMessage(viewModel.msgSignInSucceed)
                value = ViewEvents.OpenMainView
            }
        }
    }

    private fun signInCancelled() {
        finish(viewModel.msgSignInCanceled)
    }

    private fun signInFailed(errorCode: Int) {
        finish(viewModel.getMsgSignInError(errorCode))
    }


    private fun signOut() {
        // Need to re-set state in case the user
        // signs-in with an unverified email.
        viewModel.emailVerificationSent = false
        disposable.add(subscribeCompletable(
                userRepo.signOut(),
                { signOutSucceeded() },
                { signOutFailed(it) },
                schedulerProvider
        ))
    }

    private fun signOutSucceeded() {
        with(viewEventLiveData) {
            value = ViewEvents.DisplayMessage(viewModel.msgSignOutSucceed)
            value = ViewEvents.SignIn(viewModel.signInRequestCode)
        }
    }

    private fun signOutFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        with(viewEventLiveData) {
            value = ViewEvents.DisplayMessage(viewModel.msgSignOutFailed)
            value = ViewEvents.OpenMainView
        }
    }


    private fun verifyEmailButtonClicked() {
        viewEventLiveData.value = ViewEvents.HideEmailSentMessage
        verifyEmail()
    }


    private fun verifyEmail() {
        when (viewModel.emailVerificationSent) {
            true -> disposable.add(subscribeCompletable(
                    userRepo.reloadUser(),
                    { successfullyReloadedUser() },
                    { failedToReloadUser(it) },
                    schedulerProvider
            ))
            false -> disposable.add(subscribeCompletable(
                    userRepo.sendVerificationEmail(),
                    { successfullySentEmail() },
                    { failedToSendEmail(it) },
                    schedulerProvider
            ))
        }
    }

    private fun successfullyReloadedUser() {
        when (userRepo.emailVerified) {
            true -> with(viewEventLiveData) {
                value = ViewEvents.HideEmailSentMessage
                value = ViewEvents.DisplayMessage(viewModel.msgSignInSucceed)
                value = ViewEvents.OpenMainView
            }
            false -> viewEventLiveData.value =
                    ViewEvents.DisplayEmailSentMessage(userRepo.email!!)
        }
    }

    private fun failedToReloadUser(e: Throwable) {
        UtilExceptions.throwException(e)
        viewEventLiveData.value = ViewEvents.SignIn(viewModel.signInRequestCode)
    }

    private fun successfullySentEmail() {
        viewModel.emailVerificationSent = true
        viewEventLiveData.value = ViewEvents.DisplayEmailSentMessage(userRepo.email!!)
    }

    private fun failedToSendEmail(e: Throwable) {
        UtilExceptions.throwException(e)
        finish(viewModel.msgSignInError)
    }


    private fun finish(displayMessage: String) {
        disposable.clear()
        with(viewEventLiveData) {
            value = ViewEvents.DisplayMessage(displayMessage)
            value = ViewEvents.FinishView
        }
    }


    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}
