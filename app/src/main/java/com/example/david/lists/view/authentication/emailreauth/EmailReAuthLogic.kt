package com.example.david.lists.view.authentication.emailreauth

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.authentication.emailreauth.IEmailReAuthContract.ViewEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class EmailReAuthLogic(private val view: IEmailReAuthContract.View,
                       private val viewModel: IEmailReAuthContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository) :
        IEmailReAuthContract.Logic {

    override fun onEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.DeleteAcctClicked -> deleteAccount(event.password)
        }
    }


    private fun deleteAccount(password: String) {
        if (password.isBlank()) {
            view.displayError(viewModel.invalidPassword)
            return
        }

        userRepo.deleteEmailUser(
                password,
                deletionSucceeded(),
                deletionFailed()
        )
    }

    private fun deletionSucceeded() = OnSuccessListener<Void> {
        view.displayMessage(viewModel.msgAccountDeletionSucceed)
        view.openAuthView()
    }

    private fun deletionFailed() = OnFailureListener { e ->
        UtilExceptions.throwException(e)
        view.displayMessage(viewModel.msgAccountDeletionFailed)
        view.finishView()
    }
}