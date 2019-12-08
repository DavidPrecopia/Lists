package com.example.david.lists.view.reauthentication.email

import com.example.david.lists.common.subscribeCompletable
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.email.IEmailReAuthContract.ViewEvent
import com.example.domain.exception.AuthInvalidCredentialsException
import com.example.domain.exception.AuthTooManyRequestsException
import com.example.domain.repository.IRepositoryContract

class EmailReAuthLogic(private val view: IEmailReAuthContract.View,
                       private val viewModel: IEmailReAuthContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository,
                       private val schedulerProvider: ISchedulerProviderContract) :
        IEmailReAuthContract.Logic {

    override fun onEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.DeleteAcctClicked -> evalPassword(event.password.trim())
        }
    }


    private fun evalPassword(password: String) {
        when {
            password.isBlank() -> view.displayError(viewModel.msgInvalidPassword)
            else -> {
                view.displayLoading()
                deleteAccount(password)
            }
        }
    }

    private fun deleteAccount(password: String) {
        subscribeCompletable(
                userRepo.deleteEmailUser(password),
                { deletionSucceeded() },
                { deletionFailed(it) },
                schedulerProvider
        )
    }

    private fun deletionSucceeded() {
        view.displayMessage(viewModel.msgAccountDeletionSucceed)
        view.openAuthView()
    }

    private fun deletionFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        evalFailureException(e)
    }

    private fun evalFailureException(e: Throwable) {
        when (e) {
            is AuthInvalidCredentialsException -> {
                view.hideLoading()
                view.displayError(viewModel.msgInvalidPassword)
            }
            is AuthTooManyRequestsException -> {
                view.displayMessage(viewModel.msgTooManyRequest)
                view.finishView()
            }
            else -> {
                view.displayMessage(viewModel.msgAccountDeletionFailed)
                view.finishView()
            }
        }
    }
}