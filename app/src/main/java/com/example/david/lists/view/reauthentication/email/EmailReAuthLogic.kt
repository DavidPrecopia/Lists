package com.example.david.lists.view.reauthentication.email

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.email.IEmailReAuthContract.ViewEvent
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

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
            password.isBlank() -> view.displayError(viewModel.msgInvalidPassword)
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
        evalFailureException(e)
    }

    private fun evalFailureException(e: Exception) {
        when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
                view.hideLoading()
                view.displayError(viewModel.msgInvalidPassword)
            }
            is FirebaseTooManyRequestsException -> {
                view.displayMessage(viewModel.msgTooManyRequest)
                view.finishView()
            }
            else -> {
                view.displayMessage(viewModel.msgAccountDeletionFailed)
                view.finishView()
            }
        }
    }
}