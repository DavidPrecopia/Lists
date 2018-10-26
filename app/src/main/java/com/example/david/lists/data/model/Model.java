package com.example.david.lists.data.model;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.local.LocalDao;
import com.example.david.lists.data.local.LocalDatabase;
import com.example.david.lists.data.remote.IRemoteDatabaseContract;
import com.example.david.lists.data.remote.RemoteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import timber.log.Timber;

public final class Model implements IModelContract {

    private final LocalDao local;
    private final IRemoteDatabaseContract remote;

    private static volatile Model instance;

    public static Model getInstance(Application application) {
        if (instance == null) {
            synchronized (Model.class) {
                instance = new Model(application);
            }
        }
        return instance;
    }

    private Model(Application application) {
        local = LocalDatabase.getInstance(application).getLocalDao();
        remote = RemoteDatabase.getInstance();
    }


    @Override
    public Flowable<List<UserList>> getAllLists() {
        return local.getAllUserLists();
    }

    @Override
    public Flowable<List<Item>> getUserListItems(int userListId) {
        return local.getAllItems(userListId);
    }


    @Override
    public void addUserList(UserList userList) {
        long id = local.addUserList(userList);
        if (id == -1) {
            invalidId();
        } else {
            userList.setId(longToInt(id));
            remote.addUserList(userList);
        }
    }

    @Override
    public void addItem(Item item) {
        long id = local.addItem(item);
        if (id == -1) {
            invalidId();
        } else {
            item.setId(longToInt(id));
            remote.addItem(item);
        }
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        local.deleteList(getUserListsIds(userLists));
        remote.deleteUserLists(userLists);
    }

    private List<Integer> getUserListsIds(List<UserList> userLists) {
        List<Integer> userListsIds = new ArrayList<>();
        for (UserList userList : userLists) {
            userListsIds.add(userList.getId());
        }
        return userListsIds;
    }

    @Override
    public void deleteItems(List<Item> items) {
        Timber.d(String.valueOf(items.size()));
        local.deleteItem(getItemIds(items));
        remote.deleteItems(items);
    }

    private List<Integer> getItemIds(List<Item> items) {
        List<Integer> itemIds = new ArrayList<>();
        for (Item item : items) {
            itemIds.add(item.getId());
        }
        return itemIds;
    }


    @Override
    public void renameUserList(int userListId, String newTitle) {
        local.renameUserList(userListId, newTitle);
        remote.renameUserList(userListId, newTitle);
    }

    @Override
    public void renameItem(int itemId, String newTitle) {
        local.renameItem(itemId, newTitle);
        remote.renameItem(itemId, newTitle);
    }


    @Override
    public void moveUserListPosition(int userListId, int oldPosition, int newPosition) {
        if (oldPosition == newPosition) {
            return;
        }
        updatePositions(R.string.displaying_user_list, oldPosition, newPosition);
        local.moveListPosition(userListId, newPosition);
    }

    @Override
    public void moveItemPosition(int itemId, int oldPosition, int newPosition) {
        if (oldPosition == newPosition) {
            return;
        }
        updatePositions(R.string.displaying_item, oldPosition, newPosition);
        local.moveItemPosition(itemId, newPosition);
    }

    /**
     * I'm using the same method for both types in order to keep the logic DRY.
     * <p>
     * I'm decrementing oldPosition so the moved row is excluded from update operations.
     * This assumes that this method is invoked prior to the moved row being updated.
     */
    private void updatePositions(int typeResId, int oldPosition, int newPosition) {
        int correctedPosition = oldPosition - 1;

        if (newPosition > oldPosition) {
            decrementPosition(typeResId, correctedPosition, newPosition);
        } else if (newPosition < oldPosition) {
            incrementPosition(typeResId, correctedPosition, newPosition);
        }
    }

    private void decrementPosition(int typeResId, int correctedPosition, int newPosition) {
        switch (typeResId) {
            case R.string.displaying_user_list:
                local.updateUserListPositionsDecrement(correctedPosition, newPosition);
                break;
            case R.string.displaying_item:
                local.updateItemPositionsDecrement(correctedPosition, newPosition);
                break;
        }
    }

    private void incrementPosition(int typeResId, int correctedPosition, int newPosition) {
        switch (typeResId) {
            case R.string.displaying_user_list:
                local.updateUserListPositionsIncrement(correctedPosition, newPosition);
                break;
            case R.string.displaying_item:
                local.updateItemPositionsIncrement(correctedPosition, newPosition);
                break;
        }
    }


    @Override
    public void forceRefreshUserLists() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forceRefreshItems(int userListId) {
        throw new UnsupportedOperationException();
    }


    private void invalidId() {
        Timber.e("Returned row iD is invalid");
    }

    private int longToInt(long id) {
        return (int) id;
    }
}
