package com.precopia.david.lists.view.reauthentication.email

interface IEmailReAuthContract {
    interface View {
        fun openAuthView()

        fun finishView()

        fun displayMessage(message: String)

        fun displayError(message: String)

        fun displayLoading()

        fun hideLoading()
    }

    interface Logic {
        fun onEvent(event: ViewEvent)
    }

    interface ViewModel {
        val msgInvalidPassword: String

        val msgTooManyRequest: String

        val msgAccountDeletionSucceed: String

        val msgAccountDeletionFailed: String
    }

    sealed class ViewEvent {
        data class DeleteAcctClicked(val password: String) : ViewEvent()
    }
}