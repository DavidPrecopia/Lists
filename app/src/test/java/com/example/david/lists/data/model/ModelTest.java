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

import static org.mockito.Mockito.verifyZeroInteractions;

@RunWith(MockitoJUnitRunner.class)
public class ModelTest {

    @InjectMocks
    private Model model;

    @Mock
    private IRemoteStorageContract remoteStorage;


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