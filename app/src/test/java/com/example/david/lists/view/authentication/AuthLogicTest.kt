package com.example.david.lists.view.authentication

import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.authentication.IAuthContract.*
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthLogicTest {

    @Mock
    private lateinit var view: View

    @Mock
    private lateinit var viewModel: ViewModel

    @Mock
    private lateinit var userRepo: IRepositoryContract.UserRepository


    @InjectMocks
    private lateinit var logic: AuthLogic


    private val message = "message"
    private val requestCode = 100


    @Before
    fun setUp() {
    }


    /**
     * Normal behavior - [AuthGoal.SIGN_IN],
     * - Set View's result [AuthResult.AUTH_CANCELLED].
     * - Invoke [View.signIn].
     */
    @Test
    fun onStartSignIn() {
        `when`(viewModel.requestCode).thenReturn(requestCode)

        logic.onStart(AuthGoal.SIGN_IN)

        verify(view).setResult(AuthResult.AUTH_CANCELLED)
        verify(view).signIn(requestCode)
    }

    /**
     * Normal behavior - [AuthGoal.SIGN_OUT],
     * - Set View's result [AuthResult.AUTH_CANCELLED].
     * - Invoke [View.signOut].
     */
    @Test
    fun onStartSignOut() {
        logic.onStart(AuthGoal.SIGN_OUT)

        verify(view).setResult(AuthResult.AUTH_CANCELLED)
        verify(view).signOut()
    }


    /**
     * Normal behavior,
     * - Set View's result [AuthResult.AUTH_SUCCESS].
     * - Display success message.
     * - Finish the View.
     */
    @Test
    fun signInSuccessful() {
        `when`(viewModel.msgSignInSucceed).thenReturn(message)

        logic.signInSuccessful()

        verify(view).setResult(AuthResult.AUTH_SUCCESS)
        verify(view).displayMessage(message)
        verify(view).finishView()
    }


    /**
     * Normal behavior,
     * - Set View's result [AuthResult.AUTH_CANCELLED].
     * - Display canceled message.
     * - Finish the View.
     */
    @Test
    fun signInCancelled() {
        `when`(viewModel.msgSignInCanceled).thenReturn(message)

        logic.signInCancelled()

        verify(view).setResult(AuthResult.AUTH_CANCELLED)
        verify(view).displayMessage(message)
        verify(view).finishView()
    }


    /**
     * Normal behavior,
     * - Get failure message from ViewModel.
     * - Set View's result failed.
     * - Display canceled message.
     * - Finish the View.
     */
    @Test
    fun signInFailed() {
        val errorCode = 200

        `when`(viewModel.getMsgSignInError(errorCode)).thenReturn(message)

        logic.signInFailed(errorCode)

        verify(view).setResultFailed(message)
        verify(view).displayMessage(message)
        verify(view).finishView()
    }


    /**
     * Normal behavior,
     * - Set View's result [AuthResult.AUTH_SUCCESS].
     * - Display success message.
     * - Finish the View.
     */
    @Test
    fun signOutSucceeded() {
        `when`(viewModel.msgSignOutSucceed).thenReturn(message)

        logic.signOutSucceeded()

        verify(view).setResult(AuthResult.AUTH_SUCCESS)
        verify(view).displayMessage(message)
        verify(view).finishView()
    }


    /**
     * Normal behavior,
     * - Get failure message from ViewModel.
     * - Set View's result failed.
     * - Display canceled message.
     * - Finish the View.
     *
     *
     * The Exception passed to [Logic.signOutFailed]
     * should not be thrown.
     */
    @Test
    fun signOutFailed() {
        val exception = mock<Exception>()

        `when`(viewModel.msgSignOutFailed).thenReturn(message)

        logic.signOutFailed(exception)

        verify(view).setResultFailed(message)
        verify(view).displayMessage(message)
        verify(view).finishView()
    }
}