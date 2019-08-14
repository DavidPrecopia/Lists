package com.example.david.lists.view.userlistlist;

import com.example.david.lists.SchedulerProviderMockInit;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.data.repository.IRepositoryContract.Repository;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.util.IUtilNightModeContract;
import com.example.david.lists.view.authentication.IAuthContract;
import com.example.david.lists.view.authentication.IAuthContract.AuthResult;
import com.example.david.lists.view.userlistlist.IUserListViewContract.Adapter;
import com.example.david.lists.view.userlistlist.IUserListViewContract.View;
import com.example.david.lists.view.userlistlist.IUserListViewContract.ViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserListListLogicTest {

    @Mock
    private IUserListViewContract.View view;

    @Mock
    private ViewModel viewModel;

    @Mock
    private Repository repo;

    @Mock
    private IRepositoryContract.UserRepository userRepo;

    @Mock
    private ISchedulerProviderContract schedulerProvider;

    @Spy
    private CompositeDisposable disposable;

    @Mock
    private IUtilNightModeContract utilNightMode;


    @Mock
    private Adapter adapter;


    @InjectMocks
    private UserListListLogic logic;


    private String id = "id";
    private String title = "title";
    private int position = 0;

    private UserList userList = new UserList(id, new UserList(title, position));
    private UserList userListTwo = new UserList(id + 2, new UserList(title + 2, position + 1));


    private List<UserList> userListList = new ArrayList<>(Collections.singletonList(userList));

    private int invalidPosition = -1;
    private String errorMsg = "error";


    @Before
    public void setUp() {
        SchedulerProviderMockInit.INSTANCE.init(schedulerProvider);
    }


    /**
     * Normal behavior
     * - Set View state loading.
     * - Get List from repo.
     * - Save List to ViewModel.
     * - Submit the List to the View.
     * - Set View state display list.
     */
    @Test
    public void onStart() {
        when(repo.getAllUserLists()).thenReturn(Flowable.just(userListList));
        when(viewModel.getViewData()).thenReturn(userListList);

        logic.onStart();

        verify(view).setStateLoading();
        verify(viewModel).setViewData(userListList);
        verify(view).submitList(userListList);
        verify(view).setStateDisplayList();
    }

    /**
     * Error behavior - empty List returned by repo,
     * - Set View state loading.
     * - Get List from repo.
     * - Save List to ViewModel.
     * - Submit the List to the View.
     * - Set View state error with message from the ViewModel.
     */
    @Test
    public void onStartEmptyList() {
        List<UserList> emptyList = new ArrayList<>();

        when(repo.getAllUserLists()).thenReturn(Flowable.just(emptyList));
        when(viewModel.getViewData()).thenReturn(emptyList);
        when(viewModel.getErrorMsgEmptyList()).thenReturn(errorMsg);

        logic.onStart();

        verify(view).setStateLoading();
        verify(viewModel).setViewData(emptyList);
        verify(view).submitList(emptyList);
        verify(view).setStateError(errorMsg);
    }

    /**
     * Error behavior - the repo throws an error,
     * - Set View state loading.
     * - Get List from repo.
     * - Repo throws an error.
     * - Set View state error with message from ViewModel.
     */
    @Test
    public void onStartRepoThrowsError() {
        Throwable throwable = new Throwable();

        when(repo.getAllUserLists()).thenReturn(Flowable.error(throwable));
        when(viewModel.getErrorMsg()).thenReturn(errorMsg);

        logic.onStart();

        verify(view).setStateLoading();
        verify(view).setStateError(errorMsg);
    }


    /**
     * Normal behavior - valid position,
     * - Get the selected UserList with the position argument.
     * - Pass that UserList to {@link View#openUserList(UserList)}
     */
    @Test
    public void userListSelectedValidPosition() {
        when(viewModel.getViewData()).thenReturn(userListList);

        logic.userListSelected(position);

        verify(view).openUserList(userList);
    }

    /**
     * Error behavior - invalid position,
     * - Throws an exception.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void userListSelectedInvalidPosition() {
        when(viewModel.getViewData()).thenReturn(userListList);

        logic.userListSelected(invalidPosition);
    }


    /**
     * Normal behavior
     * - {@link View#openAddDialog(int)} is invoked with the current size
     * of List containing the view data.
     */
    @Test
    public void add() {
        int size = userListList.size();

        when(viewModel.getViewData()).thenReturn(userListList);

        logic.add();

        verify(view).openAddDialog(size);
    }


    /**
     * Normal behavior
     * - Get the UserList at the passed-in position.
     * - Invoked {@link View#openEditDialog(UserList)}.
     */
    @Test
    public void edit() {
        int editPosition = 0;
        List<UserList> editUserLists = new ArrayList<>();
        editUserLists.add(userList);

        when(viewModel.getViewData()).thenReturn(editUserLists);

        logic.edit(editPosition);

        verify(view).openEditDialog(userList);
    }

    /**
     * Error behavior - invalid position,
     * - Exception is thrown.
     */
    @Test(expected = IllegalArgumentException.class)
    public void editInvalidPosition() {
        logic.edit(invalidPosition);
    }


    /**
     * Normal behavior
     * - Updated the Adapter with {@link Adapter#move(int, int)}
     * - Update the ViewModel's view data.
     */
    @Test
    public void dragging() {
        int fromPos = 0;
        int toPos = 1;

        List<UserList> userListsDragging = new ArrayList<>();
        userListsDragging.add(userList);
        userListsDragging.add(userListTwo);

        when(viewModel.getViewData()).thenReturn(userListsDragging);

        logic.dragging(fromPos, toPos, adapter);

        verify(adapter).move(fromPos, toPos);
        assertThat(userListsDragging, is(contains(userListTwo, userList)));
    }


    /**
     * Normal behavior
     * - Get the moved UserList from the ViewModel.
     * - Invoke {@link Repository#updateUserListPosition(UserList, int, int)}.
     */
    @Test
    public void movedPermanently() {
        int newPosition = 0;
        List<UserList> moveUserLists = new ArrayList<>();
        moveUserLists.add(userList);

        when(viewModel.getViewData()).thenReturn(moveUserLists);

        logic.movedPermanently(newPosition);

        verify(repo).updateUserListPosition(userList, userList.getPosition(), position);
    }

    /**
     * Error behavior - invalid position,
     * - Throws an Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void movedPermanentlyInvalidPosition() {
        logic.movedPermanently(invalidPosition);
    }


    /**
     * Normal behavior
     * - Remove from the Adapter.
     * - Save the deleted UserList itself to the ViewModel's temp list.
     * - Save the deleted UserList's position to the ViewModel.
     * - Remove from the view data in the ViewModel.
     * - Notify user of deletion.
     */
    @Test
    public void delete() {
        List<UserList> tempUserLists = new ArrayList<>();

        when(viewModel.getViewData()).thenReturn(userListList);
        when(viewModel.getTempList()).thenReturn(tempUserLists);
        when(viewModel.getMsgDeletion()).thenReturn(errorMsg);

        logic.delete(position, adapter);

        assertThat(tempUserLists, is(contains(userList)));
        assertThat(userListList.isEmpty(), is(true));
        verify(adapter).remove(position);
        verify(viewModel).setTempPosition(position);
        verify(view).notifyUserOfDeletion(errorMsg);
    }

    /**
     * Error behavior - invalid position,
     * - Exception is thrown
     */
    @Test(expected = IllegalArgumentException.class)
    public void deleteInvalidPosition() {
        logic.delete(invalidPosition, adapter);
    }


    /**
     * Normal behavior
     * - Re-add to the adapter.
     * - Re-add to the view data List.
     * - Remove the last UserList from the temp List.
     * - Because the temp List is not empty, pass the temp List to the repo for deletion.
     * - Clear the temp List.
     */
    @Test
    public void undoRecentDeletion() {
        int tempPosition = 0;

        List<UserList> tempList = new ArrayList<>();
        tempList.add(userList);
        tempList.add(userListTwo);
        List<UserList> viewDataList = new ArrayList<>();

        when(viewModel.getTempList()).thenReturn(tempList);
        when(viewModel.getTempPosition()).thenReturn(tempPosition);
        when(viewModel.getViewData()).thenReturn(viewDataList);

        logic.undoRecentDeletion(adapter);

        verify(adapter).reAdd(tempPosition, userListTwo);
        verify(repo).deleteUserLists(tempList);
        assertThat(viewDataList, is(contains(userListTwo)));
        assertThat(tempList.isEmpty(), is(true));
    }

    /**
     * Error behavior - empty temp List,
     * - An exception is thrown
     */
    @Test(expected = UnsupportedOperationException.class)
    public void undoRecentDeletionEmptyList() {
        when(viewModel.getTempList()).thenReturn(new ArrayList<>());

        logic.undoRecentDeletion(adapter);
    }

    /**
     * Error behavior - invalid position,
     * - An exception is thrown
     */
    @Test(expected = UnsupportedOperationException.class)
    public void undoRecentDeletionInvalidPosition() {
        when(viewModel.getTempList()).thenReturn(userListList);
        when(viewModel.getTempPosition()).thenReturn(invalidPosition);

        logic.undoRecentDeletion(adapter);
    }


    /**
     * Normal behavior
     * - Check if the temp List is empty.
     * - Pass the temp List {@link Repository#deleteUserLists(List)}.
     * - Clear the temp List.
     */
    @Test
    public void deletionNotificationTimedOut() {
        when(viewModel.getTempList()).thenReturn(userListList);

        logic.deletionNotificationTimedOut();

        verify(repo).deleteUserLists(userListList);
        assertThat(userListList.isEmpty(), is(true));
    }

    /**
     * Error behavior - empty temp List,
     * - Check if the temp List is empty.
     * - Return without invoking any other method.
     */
    @Test
    public void deletionNotificationTimedOutEmptyTempList() {
        when(viewModel.getTempList()).thenReturn(new ArrayList<>());

        logic.deletionNotificationTimedOut();

        verify(repo, never()).deleteUserLists(anyList());
    }


    /**
     * Normal behavior
     * - Invokes {@link View#confirmSignOut()}
     */
    @Test
    public void signOut() {
        logic.signOut();

        verify(view).confirmSignOut();
    }


    /**
     * Normal behavior
     * - Invoke {@link View#openAuthentication(IAuthContract.AuthGoal, int, String)}
     */
    @Test
    public void signOutConfirmed() {
        IAuthContract.AuthGoal authGoal = IAuthContract.AuthGoal.SIGN_OUT;
        int requestCode = 100;
        String intentExtraKey = "key";

        when(viewModel.getRequestCode()).thenReturn(requestCode);
        when(viewModel.getIntentExtraAuthResultKey()).thenReturn(intentExtraKey);

        logic.signOutConfirmed();

        verify(view).openAuthentication(authGoal, requestCode, intentExtraKey);
    }


    /**
     * Normal behavior
     * - Invoke {@link View#openAuthentication(IAuthContract.AuthGoal, int, String)}
     */
    @Test
    public void signIn() {
        IAuthContract.AuthGoal authGoal = IAuthContract.AuthGoal.SIGN_IN;
        int requestCode = 100;
        String intentExtraKey = "key";

        when(viewModel.getRequestCode()).thenReturn(requestCode);
        when(viewModel.getIntentExtraAuthResultKey()).thenReturn(intentExtraKey);

        logic.signIn();

        verify(view).openAuthentication(authGoal, requestCode, intentExtraKey);
    }


    /**
     * Normal behavior
     * - {@link AuthResult#AUTH_SUCCESS} parameter
     * - Invoke {@link View#recreateView()}
     */
    @Test
    public void authResult() {
        logic.authResult(AuthResult.AUTH_SUCCESS);

        verify(view).recreateView();
    }

    /**
     * Error behavior
     * - {@link AuthResult#AUTH_FAILED} parameter
     * - Nothing invoked.
     */
    @Test
    public void authResultFailed() {
        logic.authResult(AuthResult.AUTH_FAILED);

        verify(view, never()).recreateView();
    }

    /**
     * Error behavior
     * - {@link AuthResult#AUTH_CANCELLED} parameter
     * - Nothing invoked.
     */
    @Test
    public void authResultCancelled() {
        logic.authResult(AuthResult.AUTH_CANCELLED);

        verify(view, never()).recreateView();
    }


    /**
     * Normal behavior
     * - If the menu item is unchecked, Night mode should be enabled.
     */
    @Test
    public void setNightModeUnchecked() {
        logic.setNightMode(false);

        verify(utilNightMode).setNight();
    }

    /**
     * Normal behavior
     * - If the menu item is checked, Day mode should be enabled.
     */
    @Test
    public void setNightModeChecked() {
        logic.setNightMode(true);

        verify(utilNightMode).setDay();
    }


    /**
     * Normal behavior
     * - Verify true is returned when the user is anonymous.
     */
    @Test
    public void isUserAnon() {
        boolean isAnon = true;

        when(userRepo.isAnonymous()).thenReturn(isAnon);

        assertThat(
                logic.isUserAnon(),
                is(isAnon)
        );
    }

    /**
     * Normal behavior
     * - Verify true is returned when night mode is enabled.
     */
    @Test
    public void isNightModeEnabled() {
        boolean nightModeState = true;

        when(utilNightMode.isNightModeEnabled()).thenReturn(nightModeState);

        assertThat(
                logic.isNightModeEnabled(),
                is(nightModeState)
        );
    }


    /**
     * Normal behavior
     * - Verify {@link CompositeDisposable#clear()} is invoked.
     */
    @Test
    public void onDestroy() {
        logic.onDestroy();
        verify(disposable).clear();
    }
}