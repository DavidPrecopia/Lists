package com.example.david.lists.view.authentication.googlereauth

interface IGoogleReAuthContract {
    interface View {
        fun openAuthView()

        fun finishView()

        fun displayMessage(message: String)
    }

    interface Logic {
        fun onEvent(event: ViewEvent)
    }

    interface ViewModel {
        val msgAccountDeletionSucceed: String

        val msgAccountDeletionFailed: String
    }

    sealed class ViewEvent {
        object OnStart : ViewEvent()
    }
}