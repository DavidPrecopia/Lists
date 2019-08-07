package com.example.david.lists.view.authentication;

import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.UtilExceptions;

import static com.example.david.lists.view.authentication.IAuthContract.AuthResult.AUTH_CANCELLED;
import static com.example.david.lists.view.authentication.IAuthContract.AuthResult.AUTH_SUCCESS;

public class AuthLogic implements IAuthContract.Logic {

    private final IAuthContract.View view;
    private final IAuthContract.ViewModel viewModel;

    private final IRepositoryContract.UserRepository userRepo;

    public AuthLogic(IAuthContract.View view,
                     IAuthContract.ViewModel viewModel,
                     IRepositoryContract.UserRepository userRepo) {
        this.view = view;
        this.viewModel = viewModel;
        this.userRepo = userRepo;
    }


    @Override
    public void onStart(IAuthContract.AuthGoal authGoal) {
        // In case the user cancels.
        view.setResult(AUTH_CANCELLED);
        evalAuthGoal(authGoal);
    }

    private void evalAuthGoal(IAuthContract.AuthGoal authGoal) {
        switch (authGoal) {
            case SIGN_IN:
                signIn();
                break;
            case AUTH_ANON:
                signInAnon();
                break;
            case SIGN_OUT:
                signOut();
                break;
        }
    }

    private void signIn() {
        view.signIn(viewModel.getRequestCode());
    }

    private void signInAnon() {
        // If the user is NOT anonymous, throw an Exception.
        if (!userRepo.isAnonymous()) {
            UtilExceptions.throwException(new IllegalStateException(
                    viewModel.getMsgSignInWhenNotAnon()
            ));
        }
        signIn();
    }

    @Override
    public void signInSuccessful() {
        view.setResult(AUTH_SUCCESS);
        finish(viewModel.getMsgSignInSucceed());
    }

    @Override
    public void signInCancelled() {
        view.setResult(AUTH_CANCELLED);
        finish(viewModel.getMsgSignInCanceled());
    }

    @Override
    public void signInFailed(int errorCode) {
        String reason = viewModel.getSignInErrorMsg(errorCode);
        view.setResultFailed(reason);
        finish(reason);
    }

    private void signOut() {
        view.signOut();
    }

    @Override
    public void signOutSucceeded() {
        view.setResult(AUTH_SUCCESS);
        finish(viewModel.getMsgSignOutSucceed());
    }

    @Override
    public void signOutFailed(Exception e) {
        UtilExceptions.throwException(e);
        view.setResultFailed(viewModel.getMsgSignOutFailed());
        finish(viewModel.getMsgSignOutFailed());
    }


    private void finish(String displayMessage) {
        view.displayMessage(displayMessage);
        view.finishView();
    }
}
