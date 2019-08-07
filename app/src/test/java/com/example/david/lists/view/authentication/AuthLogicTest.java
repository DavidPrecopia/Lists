package com.example.david.lists.view.authentication;

import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.view.authentication.IAuthContract.AuthGoal;
import com.example.david.lists.view.authentication.IAuthContract.AuthResult;
import com.example.david.lists.view.authentication.IAuthContract.Logic;
import com.example.david.lists.view.authentication.IAuthContract.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthLogicTest {

    @Mock
    private View view;

    @Mock
    private IAuthContract.ViewModel viewModel;

    @Mock
    private IRepositoryContract.UserRepository userRepo;


    @InjectMocks
    private AuthLogic logic;


    private String message = "message";
    private int requestCode = 100;


    @Before
    public void setUp() {
    }


    /**
     * Normal behavior - {@link AuthGoal#SIGN_IN},
     * - Set View's result {@link AuthResult#AUTH_CANCELLED}.
     * - Invoke {@link View#signIn(int)}.
     */
    @Test
    public void onStartSignIn() {
        when(viewModel.getRequestCode()).thenReturn(requestCode);

        logic.onStart(AuthGoal.SIGN_IN);

        verify(view).setResult(AuthResult.AUTH_CANCELLED);
        verify(view).signIn(requestCode);
    }

    /**
     * Normal behavior - {@link AuthGoal#SIGN_OUT},
     * - Set View's result {@link AuthResult#AUTH_CANCELLED}.
     * - Invoke {@link View#signOut()}.
     */
    @Test
    public void onStartSignOut() {
        logic.onStart(AuthGoal.SIGN_OUT);

        verify(view).setResult(AuthResult.AUTH_CANCELLED);
        verify(view).signOut();
    }

    /**
     * Normal behavior - {@link AuthGoal#AUTH_ANON},
     * - Set View's result {@link AuthResult#AUTH_CANCELLED}.
     * - Check if user is anon.
     * - Invoke {@link View#signIn(int)}.
     */
    @Test
    public void onStartAuthAnon() {
        when(viewModel.getRequestCode()).thenReturn(requestCode);
        when(userRepo.isAnonymous()).thenReturn(true);

        logic.onStart(AuthGoal.AUTH_ANON);

        verify(view).setResult(AuthResult.AUTH_CANCELLED);
        verify(view).signIn(requestCode);
    }

    /**
     * Error behavior - {@link AuthGoal#AUTH_ANON} when not anon,
     * - Set View's result {@link AuthResult#AUTH_CANCELLED}.
     * - User is not anon.
     * - Exception is thrown.
     */
    @Test(expected = IllegalStateException.class)
    public void onStartAuthAnonUserNotAnon() {
        when(userRepo.isAnonymous()).thenReturn(false);

        logic.onStart(AuthGoal.AUTH_ANON);

        verify(view).setResult(AuthResult.AUTH_CANCELLED);
        verify(view, never()).signIn(anyInt());
    }


    /**
     * Normal behavior,
     * - Set View's result {@link AuthResult#AUTH_SUCCESS}.
     * - Display success message.
     * - Finish the View.
     */
    @Test
    public void signInSuccessful() {
        when(viewModel.getMsgSignInSucceed()).thenReturn(message);

        logic.signInSuccessful();

        verify(view).setResult(AuthResult.AUTH_SUCCESS);
        verify(view).displayMessage(message);
        verify(view).finishView();
    }


    /**
     * Normal behavior,
     * - Set View's result {@link AuthResult#AUTH_CANCELLED}.
     * - Display canceled message.
     * - Finish the View.
     */
    @Test
    public void signInCancelled() {
        when(viewModel.getMsgSignInCanceled()).thenReturn(message);

        logic.signInCancelled();

        verify(view).setResult(AuthResult.AUTH_CANCELLED);
        verify(view).displayMessage(message);
        verify(view).finishView();
    }


    /**
     * Normal behavior,
     * - Get failure message from ViewModel.
     * - Set View's result failed.
     * - Display canceled message.
     * - Finish the View.
     */
    @Test
    public void signInFailed() {
        int errorCode = 200;

        when(viewModel.getMsgSignInError(errorCode)).thenReturn(message);

        logic.signInFailed(errorCode);

        verify(view).setResultFailed(message);
        verify(view).displayMessage(message);
        verify(view).finishView();
    }


    /**
     * Normal behavior,
     * - Set View's result {@link AuthResult#AUTH_SUCCESS}.
     * - Display success message.
     * - Finish the View.
     */
    @Test
    public void signOutSucceeded() {
        when(viewModel.getMsgSignOutSucceed()).thenReturn(message);

        logic.signOutSucceeded();

        verify(view).setResult(AuthResult.AUTH_SUCCESS);
        verify(view).displayMessage(message);
        verify(view).finishView();
    }


    /**
     * Normal behavior,
     * - Get failure message from ViewModel.
     * - Set View's result failed.
     * - Display canceled message.
     * - Finish the View.
     * <p>
     * The Exception passed to {@link Logic#signOutFailed(Exception)}
     * should not be thrown.
     */
    @Test
    public void signOutFailed() {
        Exception exception = new Exception();

        when(viewModel.getMsgSignOutFailed()).thenReturn(message);

        logic.signOutFailed(exception);

        verify(view).setResultFailed(message);
        verify(view).displayMessage(message);
        verify(view).finishView();
    }
}