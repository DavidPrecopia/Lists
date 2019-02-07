package com.example.david.lists.data.model;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.remote.IRemoteStorageContract;

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
public class ModelTest {

    @InjectMocks
    private Model model;

    @Mock
    private IRemoteStorageContract remoteStorage;

    private String id = "qwerty";
    private String newTitle = "New Title";
    private UserList userList = new UserList("title", 0);
    private Item item = new Item("title", 0, "qwerty");


    @Test
    public void getAllUserLists_InvokesCorrectRemoteStorageMethodOnce() {
        model.getAllUserLists();
        verify(remoteStorage, times(1)).getUserLists();
    }

    @Test
    public void getAllItems_InvokesCorrectRemoteStorageMethodOnce() {
        model.getItems(id);
        verify(remoteStorage, times(1)).getItems(id);
    }

    @Test
    public void addUserList_InvokesCorrectRemoteStorageMethodOnce() {
        model.addUserList(userList);
        verify(remoteStorage, times(1)).addUserList(userList);
    }

    @Test
    public void addItem_InvokesCorrectRemoteStorageMethodOnce() {
        model.addItem(item);
        verify(remoteStorage, times(1)).addItem(item);
    }

    @Test
    public void deleteUserLists_InvokesCorrectRemoteStorageMethodOnce() {
        List<UserList> userLists = Collections.singletonList(userList);
        model.deleteUserLists(userLists);
        verify(remoteStorage, times(1)).deleteUserLists(userLists);
    }

    @Test
    public void deleteItems_InvokesCorrectRemoteStorageMethodOnce() {
        List<Item> items = Collections.singletonList(item);
        model.deleteItems(items);
        verify(remoteStorage, times(1)).deleteItems(items);
    }

    @Test
    public void renameUserList_InvokesCorrectRemoteStorageMethodOnce() {
        model.renameUserList(id, newTitle);
        verify(remoteStorage, times(1)).renameUserList(id, newTitle);
    }

    @Test
    public void renameItem_InvokesCorrectRemoteStorageMethodOnce() {
        model.renameItem(id, newTitle);
        verify(remoteStorage, times(1)).renameItem(id, newTitle);
    }

    @Test
    public void updateUserListPosition__InvokesCorrectRemoteStorageMethodOnce() {
        int newPosition = 1;
        int oldPosition = 5;
        model.updateUserListPosition(userList, oldPosition, newPosition);
        verify(remoteStorage, times(1)).updateUserListPosition(userList, oldPosition, newPosition);
    }

    @Test
    public void updateItemPosition__InvokesCorrectRemoteStorageMethodOnce() {
        int newPosition = 1;
        int oldPosition = 5;
        model.updateItemPosition(item, oldPosition, newPosition);
        verify(remoteStorage, times(1)).updateItemPosition(item, oldPosition, newPosition);
    }


    /**ERROR TESTING**/

    @Test(expected = IllegalArgumentException.class)
    public void getItems_whenIdIsEmpty_ThrowsIllegalArgumentException() {
        model.getItems("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getItems_WhenIdIsNull_ThrowsIllegalArgumentException() {
        model.getItems(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void whenNull_AddUserList_ThrowsIllegalArgumentException() {
        model.addUserList(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNull_AddItem_ThrowsIllegalArgumentException() {
        model.addItem(null);
    }


    @Test(expected = IllegalArgumentException.class)
    public void whenListIsEmpty_DeleteUserList_ThrowsIllegalArgumentException() {
        model.deleteUserLists(new ArrayList<>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenListIsEmpty_DeleteItems_ThrowsIllegalArgumentException() {
        model.deleteItems(new ArrayList<>());
    }


    @Test(expected = IllegalArgumentException.class)
    public void whenTitleIsEmpty_RenameUserList_ThrowsIllegalArgumentException() {
        model.renameUserList("placeholder", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenTitleIsNull_RenameUserList_ThrowsIllegalArgumentException() {
        model.renameUserList("placeholder", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenIdIsEmpty_RenameUserList_ThrowsIllegalArgumentException() {
        model.renameUserList("", "newTitle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenIdIsNull_RenameUserList_ThrowsIllegalArgumentException() {
        model.renameUserList(null, "newTitle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenTitleIsEmpty_RenameItem_ThrowsIllegalArgumentException() {
        model.renameItem("placeholder", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenTitleIsNull_RenameItem_ThrowsIllegalArgumentException() {
        model.renameItem("placeholder", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenIdIsEmpty_RenameItem_ThrowsIllegalArgumentException() {
        model.renameItem("", "newTitle");
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenIdIsNull_RenameItem_ThrowsIllegalArgumentException() {
        model.renameItem(null, "newTitle");
    }


    public void whenPositionsAreTheSame_UpdateUserListPosition_Returns() {
        model.updateUserListPosition(new UserList("placeholder", 0), 5, 5);
        verifyZeroInteractions(remoteStorage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenPositionsAreNegative_UpdateUserListPosition_ThrowsIllegalArgumentException() {
        model.updateUserListPosition(new UserList("placeholder", 0), -1, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenFirstPositionIsNegative_UpdateUserListPosition_ThrowsIllegalArgumentException() {
        model.updateUserListPosition(new UserList("placeholder", 0), -1, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSecondPositionIsNegative_UpdateUserListPosition_ThrowsIllegalArgumentException() {
        model.updateUserListPosition(new UserList("placeholder", 0), 1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNullObject_UpdateUserListPosition_ThrowsIllegalArgumentException() {
        model.updateUserListPosition(null, 1, 10);
    }


    public void whenPositionsAreTheSame_UpdateItemPosition_Returns() {
        model.updateItemPosition(new Item("placeholder", 0, "qwerty"), 5, 5);
        verifyZeroInteractions(remoteStorage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenPositionsAreNegative_UpdateItemPosition_ThrowsIllegalArgumentException() {
        model.updateItemPosition(new Item("placeholder", 0, "qwerty"), -1, -10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenFirstPositionIsNegative_UpdateItemPosition_ThrowsIllegalArgumentException() {
        model.updateItemPosition(new Item("placeholder", 0, "qwerty"), -1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSecondPositionIsNegative_UpdateItemPosition_ThrowsIllegalArgumentException() {
        model.updateItemPosition(new Item("placeholder", 0, "qwerty"), 1, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenNullObject_UpdateItemPosition_ThrowsIllegalArgumentException() {
        model.updateItemPosition(null, 1, 10);
    }
}