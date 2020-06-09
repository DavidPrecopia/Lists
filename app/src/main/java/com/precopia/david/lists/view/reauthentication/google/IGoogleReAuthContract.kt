package com.precopia.david.lists.view.reauthentication.google

import androidx.lifecycle.LiveData

interface IGoogleReAuthContract {
    interface View

    interface Logic {
        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
    }

    interface ViewModel {
        val msgAccountDeletionSucceed: String

        val msgAccountDeletionFailed: String
    }


    sealed class ViewEvents {
        object OpenAuthView : ViewEvents()
        object FinishView : ViewEvents()
        data class DisplayMessage(val message: String) : ViewEvents()
    }

    sealed class LogicEvents {
        object OnStart : LogicEvents()
    }
}