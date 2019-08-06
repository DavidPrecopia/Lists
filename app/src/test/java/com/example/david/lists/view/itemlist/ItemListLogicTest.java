package com.example.david.lists.view.itemlist;

import com.example.david.lists.SchedulerProviderMockInit;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.repository.IRepositoryContract.Repository;
import com.example.david.lists.util.ISchedulerProviderContract;
import com.example.david.lists.view.itemlist.IItemViewContract.Adapter;
import com.example.david.lists.view.itemlist.IItemViewContract.View;

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
public class ItemListLogicTest {

    @Mock
    private View view;

    @Mock
    private IItemViewContract.ViewModel viewModel;

    @Mock
    private Repository repo;

    @Mock
    private ISchedulerProviderContract schedulerProvider;

    @Spy
    private CompositeDisposable disposable;


    @InjectMocks
    private ItemListLogic logic;


    @Mock
    private Adapter adapter;

    private String userListId = "qwerty";

    private String id = "id_one";
    private String title = "title_one";
    private int position = 0;
    private Item item = new Item(id, new Item(title, position, userListId));

    private String id_two = "id_two";
    private String title_two = "title_two";
    private int position_two = 1;
    private Item item_two = new Item(id_two, new Item(title_two, position_two, userListId));

    private List<Item> itemList = new ArrayList<>(Collections.singletonList(item));

    private int invalidPosition = -1;
    private String errorMsg = "error";


    @Before
    public void setUp() {
        SchedulerProviderMockInit.init(schedulerProvider);
    }


    /**
     * Normal behavior
     * - Set View state loading.
     * - Observe deleted UserLists Flowable from repo.
     * - Get List from repo.
     * - Save List to ViewModel.
     * - Submit the List to the View.
     * - Set View state display list.
     */
    @Test
    public void onStart() {
        when(viewModel.getUserListId()).thenReturn(userListId);
        when(viewModel.getViewData()).thenReturn(itemList);
        when(repo.getEventUserListDeleted()).thenReturn(Flowable.just(new ArrayList<>()));
        when(repo.getItems(userListId)).thenReturn(Flowable.just(itemList));

        logic.onStart();

        verify(view).setStateLoading();
        verify(viewModel).setViewData(itemList);
        verify(view).submitList(itemList);
        verify(view).setStateDisplayList();
    }

    /**
     * Error behavior - empty List from repo,
     * - Set View state loading.
     * - Observe deleted UserLists Flowable from repo.
     * - Get List from repo.
     * - Save List to ViewModel.
     * - Submit the List to the View.
     * - Set View state error with message from the ViewModel.
     */
    @Test
    public void onStartEmptyList() {
        List<Item> emptyList = new ArrayList<>();

        when(viewModel.getUserListId()).thenReturn(userListId);
        when(viewModel.getViewData()).thenReturn(emptyList);
        when(viewModel.getErrorMsgEmptyList()).thenReturn(errorMsg);
        when(repo.getEventUserListDeleted()).thenReturn(Flowable.just(new ArrayList<>()));
        when(repo.getItems(userListId)).thenReturn(Flowable.just(emptyList));

        logic.onStart();

        verify(view).setStateLoading();
        verify(viewModel).setViewData(emptyList);
        verify(view).submitList(emptyList);
        verify(view).setStateError(errorMsg);
    }

    /**
     * Error behavior - repo throws an error,
     * - Set View state loading.
     * - Observe deleted UserLists Flowable from repo.
     * - Get List from repo.
     * - Repo throws an error.
     * - Set View state error with message from the ViewModel.
     */
    @Test
    public void onStartRepoThrowsError() {
        Throwable throwable = new Throwable();

        when(viewModel.getUserListId()).thenReturn(userListId);
        when(viewModel.getErrorMsg()).thenReturn(errorMsg);
        when(repo.getEventUserListDeleted()).thenReturn(Flowable.just(new ArrayList<>()));
        when(repo.getItems(userListId)).thenReturn(Flowable.error(throwable));

        logic.onStart();

        verify(view).setStateLoading();
        verify(view).setStateError(errorMsg);
    }


    /**
     * Normal behavior
     * - Invoke {@link View#openAddDialog(String, int)} with the
     * UserListId and the view data's List size.
     */
    @Test
    public void add() {
        when(viewModel.getUserListId()).thenReturn(userListId);
        when(viewModel.getViewData()).thenReturn(itemList);

        logic.add();

        verify(view).openAddDialog(userListId, itemList.size());
    }


    /**
     * Normal behavior
     * - Get the Item at the passed-in position.
     * - Invoke {@link View#openEditDialog(Item)}.
     */
    @Test
    public void edit() {
        int editPosition = 0;
        List<Item> editItems = new ArrayList<>();
        editItems.add(item);

        when(viewModel.getViewData()).thenReturn(editItems);

        logic.edit(editPosition);

        verify(view).openEditDialog(item);
    }

    /**
     * Error behavior - invalid position,
     * - Throw an Exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void editInvalidPosition() {
        logic.edit(invalidPosition);
    }


    /**
     * Normal behavior
     * - Update the Adapter with {@link Adapter#move(int, int)}.
     * - Update the view data.
     */
    @Test
    public void dragging() {
        int fromPos = 0;
        int toPos = 1;

        List<Item> draggingItems = new ArrayList<>();
        draggingItems.add(item);
        draggingItems.add(item_two);

        when(viewModel.getViewData()).thenReturn(draggingItems);

        logic.dragging(fromPos, toPos, adapter);

        verify(adapter).move(fromPos, toPos);
        assertThat(draggingItems, is(contains(item_two, item)));
    }


    /**
     * Normal behavior
     * - Gets Item at passed-in position.
     * - Invokes {@link Repository#updateItemPosition(Item, int, int)}
     */
    @Test
    public void movedPermanently() {
        int newPosition = 0;
        List<Item> moveItems = new ArrayList<>();
        moveItems.add(item);

        when(viewModel.getViewData()).thenReturn(moveItems);

        logic.movedPermanently(newPosition);

        verify(repo).updateItemPosition(item, item.getPosition(), newPosition);
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
     * Normal behavior,
     * - Remove from the Adapter.
     * - Save the deleted UserList itself to the ViewModel's temp list.
     * - Save the deleted UserList's position to the ViewModel.
     * - Remove from the view data in the ViewModel.
     * - Notify user of deletion.
     */
    @Test
    public void delete() {
        int deletedPosition = 0;
        List<Item> deletedItems = new ArrayList<>();
        deletedItems.add(item);
        List<Item> tempList = new ArrayList<>();

        when(viewModel.getViewData()).thenReturn(deletedItems);
        when(viewModel.getTempList()).thenReturn(tempList);
        when(viewModel.getMsgItemDeleted()).thenReturn(errorMsg);

        logic.delete(deletedPosition, adapter);

        verify(adapter).remove(position);
        verify(viewModel).setTempPosition(deletedPosition);
        verify(view).notifyUserOfDeletion(errorMsg);
        assertThat(deletedItems.isEmpty(), is(true));
        assertThat(tempList, is(contains(item)));
    }

    /**
     * Error behavior - invalid position,
     * - Throws an exception.
     */
    @Test(expected = IllegalArgumentException.class)
    public void deleteInvalidPosition() {
        logic.delete(invalidPosition, adapter);
    }


    /**
     * Normal behavior
     * - Re-add to the adapter.
     * - Re-add to the view data List.
     * - Remove the last Item from the temp List.
     * - Because the temp List is not empty, pass the temp List to the repo for deletion.
     * - Clear the temp List.
     */
    @Test
    public void undoRecentDeletion() {
        int undoPosition = 0;
        List<Item> tempList = new ArrayList<>();
        tempList.add(item);
        tempList.add(item_two);
        List<Item> viewDataList = new ArrayList<>();

        when(viewModel.getTempList()).thenReturn(tempList);
        when(viewModel.getTempPosition()).thenReturn(undoPosition);
        when(viewModel.getViewData()).thenReturn(viewDataList);

        logic.undoRecentDeletion(adapter);

        verify(adapter).reAdd(undoPosition, item_two);
        verify(repo).deleteItems(tempList);
        assertThat(viewDataList, is(contains(item_two)));
        assertThat(tempList.isEmpty(), is(true));
    }

    /**
     * Error behavior - empty temp List,
     * - Throws an Exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void undoRecentDeletionEmptyTempList() {
        when(viewModel.getTempList()).thenReturn(new ArrayList<>());

        logic.undoRecentDeletion(adapter);
    }

    /**
     * Error behavior - invalid temp position,
     * - Throws an Exception.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void undoRecentDeletionInvalidTempPosition() {
        when(viewModel.getTempList()).thenReturn(itemList);
        when(viewModel.getTempPosition()).thenReturn(invalidPosition);

        logic.undoRecentDeletion(adapter);
    }


    /**
     * Normal behavior
     * - Check if the temp List is empty.
     * - Pass the temp List {@link Repository#deleteItems(List)}.
     * - Clear the temp List.
     */
    @Test
    public void deletionNotificationTimedOut() {
        when(viewModel.getTempList()).thenReturn(itemList);

        logic.deletionNotificationTimedOut();

        verify(repo).deleteItems(itemList);
        assertThat(itemList.isEmpty(), is(true));
    }

    /**
     * Error behavior - empty temp List,
     * - Method returns without invoking the repo.
     */
    @Test
    public void deletionNotificationTimedOutEmptyTempList() {
        when(viewModel.getTempList()).thenReturn(new ArrayList<>());

        logic.deletionNotificationTimedOut();

        verify(repo, never()).deleteItems(anyList());
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