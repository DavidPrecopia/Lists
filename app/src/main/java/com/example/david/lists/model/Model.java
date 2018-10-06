package com.example.david.lists.model;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.database.ListsDao;
import com.example.david.lists.database.ListsDatabase;
import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;

public final class Model implements IModelContract {

    private final ListsDao dao;

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
        dao = ListsDatabase.getInstance(application).getListDao();
    }


    @Override
    public Flowable<List<UserList>> getAllLists() {
        return dao.getAllLists();
    }

    @Override
    public Flowable<List<Item>> getListItems(int listId) {
        return dao.getListItems(listId);
    }


    @Override
    public void addList(UserList list) {
        dao.addList(list);
    }

    @Override
    public void addItem(Item item) {
        dao.addItem(item);
    }


    @Override
    public void deleteList(int listId) {
        dao.deleteList(listId);
    }

    @Override
    public void deleteItem(int itemId) {
        dao.deleteItem(itemId);
    }


    @Override
    public void changeListTitle(int listId, String newTitle) {
        dao.changeListTitle(listId, newTitle);
    }

    @Override
    public void changeItemTitle(int itemId, String newTitle) {
        dao.changeItemTitle(itemId, newTitle);
    }


    @Override
    public void moveUserListPosition(int listId, int oldPosition, int newPosition) {
        if (oldPosition == newPosition) {
            return;
        }
        updatePositions(R.string.displaying_user_list, oldPosition, newPosition);
        dao.moveListPosition(listId, newPosition);
    }

    @Override
    public void moveItemPosition(int itemId, int oldPosition, int newPosition) {
        if (oldPosition == newPosition) {
            return;
        }
        updatePositions(R.string.displaying_item, oldPosition, newPosition);
        dao.moveItemPosition(itemId, newPosition);
    }

    /**
     * I'm using the same method for both types in order to keep the logic DRY
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
                dao.updateUserListPositionsDecrement(correctedPosition, newPosition);
                break;
            case R.string.displaying_item:
                dao.updateItemPositionsDecrement(correctedPosition, newPosition);
                break;
        }
    }

    private void incrementPosition(int typeResId, int correctedPosition, int newPosition) {
        switch (typeResId) {
            case R.string.displaying_user_list:
                dao.updateUserListPositionsIncrement(correctedPosition, newPosition);
                break;
            case R.string.displaying_item:
                dao.updateItemPositionsIncrement(correctedPosition, newPosition);
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
