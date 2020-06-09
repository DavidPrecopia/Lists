package com.precopia.david.lists.view.reauthentication.google

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.precopia.david.lists.common.subscribeCompletable
import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.david.lists.util.UtilExceptions
import com.precopia.david.lists.view.reauthentication.google.IGoogleReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvents
import com.precopia.domain.repository.IRepositoryContract

class GoogleReAuthLogic(private val viewModel: IGoogleReAuthContract.ViewModel,
                        private val userRepo: IRepositoryContract.UserRepository,
                        private val schedulerProvider: ISchedulerProviderContract) :
        ViewModel(),
        IGoogleReAuthContract.Logic {

    private val viewEventLiveData = MutableLiveData<ViewEvents>()


    override fun onEvent(event: LogicEvents) {
        when (event) {
            LogicEvents.OnStart -> deleteAccount()
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
        viewEvent(ViewEvents.DisplayMessage(viewModel.msgAccountDeletionSucceed))
        viewEvent(ViewEvents.OpenAuthView)
    }

    private fun accountDeletionFailed(e: Throwable) {
        UtilExceptions.throwException(e)
        viewEvent(ViewEvents.DisplayMessage(viewModel.msgAccountDeletionFailed))
        viewEvent(ViewEvents.FinishView)
    }


    private fun viewEvent(event: ViewEvents) {
        viewEventLiveData.value = event
    }

    override fun observe(): LiveData<ViewEvents> = viewEventLiveData
}