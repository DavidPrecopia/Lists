package com.precopia.david.lists.view.reauthentication.google

import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvent
import com.precopia.domain.repository.IRepositoryContract

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