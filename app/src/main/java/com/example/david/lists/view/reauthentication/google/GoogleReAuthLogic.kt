package com.example.david.lists.view.reauthentication.google

import com.example.david.lists.common.subscribeCompletable
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvent
import com.example.domain.repository.IRepositoryContract

class GoogleReAuthLogic(private val view: IGoogleReAuthContract.View,
                        private val viewModel: IGoogleReAuthContract.ViewModel,
                        private val userRepo: IRepositoryContract.UserRepository,
                        private val schedulerProvider: ISchedulerProviderContract) :
        IGoogleReAuthContract.Logic {

    override fun onEvent(event: ViewEvent) {
        when (event) {
            ViewEvent.OnStart -> deleteAccount()
        }
    }


    private fun deleteAccount() {
        subscribeCompletable(
                userRepo.deleteGoogleUser(),
                { accountDeletionSucceeded() },
                { accountDeletionFailed(it) },
                schedulerProvider
        )
    }

    private fun accountDeletionSucceeded() {
        view.displayMessage(viewModel.msgAccountDeletionSucceed)
        view.openAuthView()
    }

    private fun accountDeletionFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        view.displayMessage(viewModel.msgAccountDeletionFailed)
        view.finishView()
    }
}