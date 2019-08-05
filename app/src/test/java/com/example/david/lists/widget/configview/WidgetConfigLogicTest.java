package com.example.david.lists.widget.configview;

import com.example.david.lists.SchedulerProviderMockInit;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;

import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WidgetConfigLogicTest {

    @Mock
    private IWidgetConfigContract.View view;

    @Mock
    private IWidgetConfigContract.ViewModel viewModel;

    @Mock
    private IRepositoryContract.Repository repo;

    @Mock
    private ISchedulerProviderContract schedulerProvider;

    @Spy
    private CompositeDisposable disposable;


    @InjectMocks
    private WidgetConfigLogic logic;


    private int position = 0;
    private String title = "title";
    private String id = "qwerty";
    private UserList userList = new UserList(id, new UserList(title, position));

    private int widgetIdValid = 100;
    private int widgetIdInvalid = -1;

    private int resultCanceled = 0;


    @Before
    public void setUp() {
        SchedulerProviderMockInit.init(schedulerProvider);
    }


    /**
     * Expected, normal behavior - when a valid widget ID is passed-in,
     * - Save the widget ID to the ViewModel.
     * - Set the View's result to cancelled.
     * - Set the View's state to loading.
     * - Get the List of UserLists from the repo.
     * - Save the List to the ViewModel.
     * - Set the View's state to display list.
     */
    @Test
    public void onStartWithValidWidgetId() {
        List<UserList> userLists = new ArrayList<>();
        userLists.add(userList);

        when(viewModel.getInvalidWidgetId()).thenReturn(widgetIdInvalid);
        when(viewModel.getResultCancelled()).thenReturn(resultCanceled);
        when(viewModel.getViewData()).thenReturn(userLists);
        when(repo.getAllUserLists()).thenReturn(Flowable.just(userLists));

        logic.onStart(widgetIdValid);

        verify(viewModel).setWidgetId(widgetIdValid);
        verify(view).setResults(widgetIdValid, resultCanceled);
        verify(view).setStateLoading();
        verify(viewModel).setViewData(userLists);
        verify(view).setStateDisplayList();
    }

    /**
     * Error behavior - when an in-valid widget ID is passed-in,
     * - Save the widget ID to the ViewModel.
     * - Set the View's result to cancelled.
     * - Finish the View.
     * <p>
     * Unlike the real code, the code in the method under test will continue to
     * execute after the View has been told to finished,
     * thus I need to stub the methods that are called post finish.
     */
    @Test
    public void onStartWithInvalidWidgetId() {
        when(viewModel.getViewData()).thenReturn(new ArrayList<>());
        when(repo.getAllUserLists()).thenReturn(Flowable.just(new ArrayList<>()));

        when(viewModel.getInvalidWidgetId()).thenReturn(widgetIdInvalid);
        when(viewModel.getResultCancelled()).thenReturn(resultCanceled);

        logic.onStart(widgetIdInvalid);

        verify(viewModel).setWidgetId(widgetIdInvalid);
        verify(view).setResults(widgetIdInvalid, resultCanceled);
        verify(view).finishViewInvalidId();
    }

    /**
     * Error behavior - when the repo returns an empty List,
     * - Save the widget ID to the ViewModel.
     * - Set the View's result to cancelled.
     * - Set the View's state to loading.
     * - Get the List of UserLists from the repo.
     * - Save the List to the ViewModel.
     * - Set the View's state to display error with message from ViewModel.
     */
    @Test
    public void onStartEmptyList() {
        List<UserList> userLists = new ArrayList<>();
        String emptyListError = "error";

        when(viewModel.getInvalidWidgetId()).thenReturn(widgetIdInvalid);
        when(viewModel.getResultCancelled()).thenReturn(resultCanceled);
        when(viewModel.getViewData()).thenReturn(userLists);
        when(viewModel.getErrorMsgEmptyList()).thenReturn(emptyListError);
        when(repo.getAllUserLists()).thenReturn(Flowable.just(userLists));

        logic.onStart(widgetIdValid);

        verify(viewModel).setWidgetId(widgetIdValid);
        verify(view).setResults(widgetIdValid, resultCanceled);
        verify(view).setStateLoading();
        verify(viewModel).setViewData(userLists);
        verify(view).setStateError(emptyListError);
    }

    /**
     * Error behavior - when the repo throws an error,
     * - Save the widget ID to the ViewModel.
     * - Set the View's result to cancelled.
     * - Set the View's state to loading.
     * - Get the List of UserLists from the repo.
     * - Repo thrown an error
     * - Set the View's state to display error with message from ViewModel.
     */
    @Test
    public void OnStartRepoThrowsError() {
        Throwable throwable = new Throwable();
        String error = "error";

        when(viewModel.getInvalidWidgetId()).thenReturn(widgetIdInvalid);
        when(viewModel.getResultCancelled()).thenReturn(resultCanceled);
        when(viewModel.getErrorMsg()).thenReturn(error);
        when(repo.getAllUserLists()).thenReturn(Flowable.error(throwable));

        logic.onStart(widgetIdValid);

        verify(viewModel).setWidgetId(widgetIdValid);
        verify(view).setResults(widgetIdValid, resultCanceled);
        verify(view).setStateLoading();
        verify(view).setStateError(error);
    }


    /**
     * Normal behavior.
     * - Save the details of the selected UserList via a View method.
     * - Set the View's result to successful.
     * - Finish the View.
     */
    @Test
    public void selectedUserList() {
        int resultOk = 1;
        String sharedPrefId = "id";
        String sharedPrefTitle = "title";

        when(viewModel.getWidgetId()).thenReturn(widgetIdValid);
        when(viewModel.getResultOk()).thenReturn(resultOk);
        when(viewModel.getSharedPrefKeyId()).thenReturn(sharedPrefId);
        when(viewModel.getSharedPrefKeyTitle()).thenReturn(sharedPrefTitle);

        logic.selectedUserList(userList);

        verify(view).saveDetails(userList.getId(), userList.getTitle(), sharedPrefId, sharedPrefTitle);
        verify(view).setResults(widgetIdValid, resultOk);
        verify(view).finishView(widgetIdValid);
    }

    /**
     * Error behavior - null passed-in.
     * Should throw a NPE.
     */
    @Test(expected = NullPointerException.class)
    public void selectedUserListNullPassedIn() {
        logic.selectedUserList(null);
    }


    /**
     * Normal behavior.
     * Verify the CompositeDisposable is cleared.
     */
    @Test
    public void onDestroy() {
        logic.onDestroy();
        verify(disposable, atMostOnce()).clear();
    }
}
