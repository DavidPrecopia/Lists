package com.example.david.lists.model;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.database.LocalDao;
import com.example.david.lists.database.LocalDatabase;
import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public final class Model implements IModelContract {

    private final LocalDao local;

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
        local = LocalDatabase.getInstance(application).getListDao();
    }


    @Override
    public Flowable<List<UserList>> getAllLists() {
        return local.getAllLists();
    }

    @Override
    public Flowable<List<Item>> getUserListItems(int listId) {
        return local.getListItems(listId);
    }


    @Override
    public void addList(UserList list) {
        local.addList(list);
    }

    @Override
    public void addItem(Item item) {
        local.addItem(item);
    }


    @Override
    public void deleteList(List<Integer> listIds) {
        local.deleteList(listIds);
    }

    @Override
    public void deleteItem(List<Integer> itemIds) {
        local.deleteItem(itemIds);
    }


    @Override
    public void changeListTitle(int listId, String newTitle) {
        local.changeListTitle(listId, newTitle);
    }

    @Override
    public void changeItemTitle(int itemId, String newTitle) {
        local.changeItemTitle(itemId, newTitle);
    }


    @Override
    public void moveUserListPosition(int listId, int oldPosition, int newPosition) {
        if (oldPosition == newPosition) {
            return;
        }
        updatePositions(R.string.displaying_user_list, oldPosition, newPosition);
        local.moveListPosition(listId, newPosition);
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
     *
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
    public void forceRefreshItems(int listId) {
        throw new UnsupportedOperationException();
    }
}
