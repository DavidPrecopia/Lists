package com.example.david.lists.view.reauthentication.email

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.email.IEmailReAuthContract.ViewEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class EmailReAuthLogic(private val view: IEmailReAuthContract.View,
                       private val viewModel: IEmailReAuthContract.ViewModel,
                       private val userRepo: IRepositoryContract.UserRepository) :
        IEmailReAuthContract.Logic {

    override fun onEvent(event: ViewEvent) {
        when (event) {
            is ViewEvent.DeleteAcctClicked -> evalPassword(event.password.trim())
        }
    }


    private fun evalPassword(password: String) {
        when {
            password.isBlank() -> view.displayError(viewModel.invalidPassword)
            else -> {
                view.displayLoading()
                deleteAccount(password)
            }
        }
    }

    private fun deleteAccount(password: String) {
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