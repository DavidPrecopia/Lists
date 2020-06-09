package com.precopia.david.lists.view.reauthentication.email

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract.ViewEvents
import com.precopia.domain.exception.AuthInvalidCredentialsException
import com.precopia.domain.exception.AuthTooManyRequestsException
import com.precopia.domain.repository.IRepositoryContract

class EmailReAuthLogic(private val viewModel: IEmailReAuthContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository,
                       private val schedulerProvider: ISchedulerProviderContract) :
        ViewModel(),
        IEmailReAuthContract.Logic {

    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(event: LogicEvents) {
        when (event) {
            is LogicEvents.DeleteAcctClicked -> evalPassword(event.password.trim())
        }
    }


    private fun evalPassword(password: String) {
        when {
            password.isBlank() -> viewEvent(
                    ViewEvents.DisplayError(viewModel.msgInvalidPassword)
            )
            else -> {
                viewEvent(ViewEvents.DisplayLoading)
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
        viewEvent(ViewEvents.DisplayMessage(viewModel.msgAccountDeletionSucceed))
        viewEvent(ViewEvents.OpenAuthView)
    }

    private fun deletionFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        evalFailureException(e)
    }

    private fun evalFailureException(e: Throwable) {
        when (e) {
            is AuthInvalidCredentialsException -> {
                viewEvent(ViewEvents.HideLoading)
                viewEvent(ViewEvents.DisplayError(viewModel.msgInvalidPassword))
            }
            is AuthTooManyRequestsException -> {
                viewEvent(ViewEvents.DisplayMessage(viewModel.msgTooManyRequest))
                viewEvent(ViewEvents.FinishView)
            }
            else -> {
                viewEvent(ViewEvents.DisplayMessage(viewModel.msgAccountDeletionFailed))
                viewEvent(ViewEvents.FinishView)
            }
        }
    }


    private fun viewEvent(events: ViewEvents) {
        viewEventLiveData.value = events
    }

    override fun observe(): LiveData<ViewEvents> = viewEventLiveData
}