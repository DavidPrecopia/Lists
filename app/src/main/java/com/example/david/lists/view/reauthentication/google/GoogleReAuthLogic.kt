package com.example.david.lists.view.reauthentication.google

import com.example.david.lists.util.UtilExceptions
import com.example.david.lists.view.reauthentication.google.IGoogleReAuthContract.ViewEvent
import com.example.domain.repository.IRepositoryContract
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener

class GoogleReAuthLogic(private val view: IGoogleReAuthContract.View,
                        private val viewModel: IGoogleReAuthContract.ViewModel,
                        private val userRepo: IRepositoryContract.UserRepository) :
        IGoogleReAuthContract.Logic {

    override fun onEvent(event: ViewEvent) {
        when (event) {
            ViewEvent.OnStart -> deleteAccount()
        }
    }


    private fun deleteAccount() {
        userRepo.deleteGoogleUser(
                accountDeletionSucceeded(),
                accountDeletionFailed()
        )
    }

    private fun accountDeletionSucceeded() = OnSuccessListener<Void> {
        view.displayMessage(viewModel.msgAccountDeletionSucceed)
        view.openAuthView()
    }

    private fun accountDeletionFailed() = OnFailureListener { e ->
        UtilExceptions.throwException(e)
        view.displayMessage(viewModel.msgAccountDeletionFailed)
        view.finishView()
    }
}