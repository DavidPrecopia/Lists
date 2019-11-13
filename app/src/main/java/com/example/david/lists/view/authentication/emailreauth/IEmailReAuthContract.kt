package com.example.david.lists.view.authentication.emailreauth

interface IEmailReAuthContract {
    interface View {
        fun openAuthView()

        fun openMainView()

        fun displayMessage(message: String)

        fun displayError(message: String)
    }

    interface Logic {
        fun onEvent(event: ViewEvent)
    }

    interface ViewModel {
        val invalidPassword: String

        val msgAccountDeletionSucceed: String

        val msgAccountDeletionFailed: String
    }

    sealed class ViewEvent {
        data class DeleteAcctClicked(val password: String) : ViewEvent()
        object CancelClicked : ViewEvent()
    }
}