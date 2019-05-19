package com.example.david.lists.data.repository;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.remote.IRemoteRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryImplTest {

    @InjectMocks
    private RepositoryImpl repositoryImpl;

    @Mock
    private IRemoteRepository remoteDatabase;

    private final String id = "qwerty";
    private final String newTitle = "New Title";
    private final UserList userList = new UserList("title", 0);
    private final Item item = new Item("title", 0, "qwerty");


    @Test
    public void getAllUserLists_InvokesCorrectRemoteStorageMethodOnce() {
        repositoryImpl.getAllUserLists();
        verify(remoteDatabase, times(1)).getUserLists();
    }

    @Test
    public void getAllItems_InvokesCorrectRemoteStorageMethodOnce() {
        repositoryImpl.getItems(id);
        verify(remoteDatabase, times(1)).getItems(id);
    }

    @Test
    public void addUserList_InvokesCorrectRemoteStorageMethodOnce() {
        repositoryImpl.addUserList(userList);
        verify(remoteDatabase, times(1)).addUserList(userList);
    }

    @Test
    public void addItem_InvokesCorrectRemoteStorageMethodOnce() {
        repositoryImpl.addItem(item);
        verify(remoteDatabase, times(1)).addItem(item);
    }

    @Test
    public void deleteUserLists_InvokesCorrectRemoteStorageMethodOnce() {
        List<UserList> userLists = Collections.singletonList(userList);
        repositoryImpl.deleteUserLists(userLists);
        verify(remoteDatabase, times(1)).deleteUserLists(userLists);
    }

    @Test
    public void deleteItems_InvokesCorrectRemoteStorageMethodOnce() {
        List<Item> items = Collections.singletonList(item);
        repositoryImpl.deleteItems(items);
        verify(remoteDatabase, times(1)).deleteItems(items);
    }

    @Test
    public void renameUserList_InvokesCorrectRemoteStorageMethodOnce() {
        repositoryImpl.renameUserList(id, newTitle);
        verify(remoteDatabase, times(1)).renameUserList(id, newTitle);
    }

    @Test
    public void renameItem_InvokesCorrectRemoteStorageMethodOnce() {
        repositoryImpl.renameItem(id, newTitle);
        verify(remoteDatabase, times(1)).renameItem(id, newTitle);
    }

    @Test
    public void updateUserListPosition__InvokesCorrectRemoteStorageMethodOnce() {
        int newPosition = 1;
        int oldPosition = 5;
        repositoryImpl.updateUserListPosition(userList, oldPosition, newPosition);
        verify(remoteDatabase, times(1)).updateUserListPosition(userList, oldPosition, newPosition);
    }

    @Test
    public void updateItemPosition__InvokesCorrectRemoteStorageMethodOnce() {
        int newPosition = 1;
        int oldPosition = 5;
        repositoryImpl.updateItemPosition(item, oldPosition, newPosition);
        verify(remoteDatabase, times(1)).updateItemPosition(item, oldPosition, newPosition);
    }


    /**ERROR TESTING**/

    @Test(expected = IllegalArgumentException.class)
    public void getItems_whenIdIsEmpty_ThrowsIllegalArgumentException() {
        repositoryImpl.getItems("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getItems_WhenIdIsNull_ThrowsIllegalArgumentException() {
        repositoryImpl.getItems(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void addUserList_WhenNull_ThrowsIllegalArgumentException() {
        repositoryImpl.addUserList(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addItem_WhenNull_ThrowsIllegalArgumentException() {
        repositoryImpl.addItem(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void deleteUserList_WhenListIsEmpty_ThrowIllegalArgumentException() {
        repositoryImpl.deleteUserLists(new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteItems_WhenListIsEmpty_ThrowIllegalArgumentException() {
        repositoryImpl.deleteItems(new ArrayList<>());
    }


    @Test(expected = IllegalArgumentException.class)
    public void renameUserList_WhenTitleIsEmpty_ThrowsIllegalArgumentException() {
        repositoryImpl.renameUserList("placeholder", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameUserList_WhenTitleIsNull_ThrowsIllegalArgumentException() {
        repositoryImpl.renameUserList("placeholder", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameUserList_WhenIdIsEmpty_ThrowsIllegalArgumentException() {
        repositoryImpl.renameUserList("", "newTitle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameUserList_WhenIdIsNull_ThrowsIllegalArgumentException() {
        repositoryImpl.renameUserList(null, "newTitle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameItem_WhenTitleIsEmpty_ThrowsIllegalArgumentException() {
        repositoryImpl.renameItem("placeholder", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameItem_WhenTitleIsNull_ThrowsIllegalArgumentException() {
        repositoryImpl.renameItem("placeholder", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameItem_WhenIdIsEmpty_ThrowsIllegalArgumentException() {
        repositoryImpl.renameItem("", "newTitle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void renameItem_WhenIdIsNull_ThrowsIllegalArgumentException() {
        repositoryImpl.renameItem(null, "newTitle");
    }


    public void updateUserListPosition_WhenPositionsAreTheSame_Returns() {
        repositoryImpl.updateUserListPosition(new UserList("placeholder", 0), 5, 5);
        verifyZeroInteractions(remoteDatabase);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserListPosition_WhenPositionsAreNegative_ThrowsIllegalArgumentException() {
        repositoryImpl.updateUserListPosition(new UserList("placeholder", 0), -1, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserListPosition_WhenFirstPositionIsNegative_ThrowsIllegalArgumentException() {
        repositoryImpl.updateUserListPosition(new UserList("placeholder", 0), -1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserListPosition_WhenSecondPositionIsNegative_ThrowsIllegalArgumentException() {
        repositoryImpl.updateUserListPosition(new UserList("placeholder", 0), 1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateUserListPosition_WhenNullObject_ThrowsIllegalArgumentException() {
        repositoryImpl.updateUserListPosition(null, 1, 10);
    }


    public void updateItemPosition_WhenPositionsAreTheSame_Returns() {
        repositoryImpl.updateItemPosition(new Item("placeholder", 0, "qwerty"), 5, 5);
        verifyZeroInteractions(remoteDatabase);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateItemPosition_WhenPositionsAreNegative_ThrowsIllegalArgumentException() {
        repositoryImpl.updateItemPosition(new Item("placeholder", 0, "qwerty"), -1, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateItemPosition_WhenFirstPositionIsNegative_ThrowsIllegalArgumentException() {
        repositoryImpl.updateItemPosition(new Item("placeholder", 0, "qwerty"), -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateItemPosition_WhenSecondPositionIsNegative_ThrowsIllegalArgumentException() {
        repositoryImpl.updateItemPosition(new Item("placeholder", 0, "qwerty"), 1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateItemPosition_WhenNullObject_ThrowsIllegalArgumentException() {
        repositoryImpl.updateItemPosition(null, 1, 10);
    }
}