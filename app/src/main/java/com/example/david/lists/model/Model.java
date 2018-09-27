package com.example.david.lists.model;

import android.app.Application;

import com.example.david.lists.database.ListsDao;
import com.example.david.lists.database.ListsDatabase;
import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import java.util.List;

import io.reactivex.Flowable;
import timber.log.Timber;

public final class Model implements IModelContract {

    private final ListsDao dao;

    private static Model model;

    public static Model getInstance(Application application) {
        if (model == null) {
            model = new Model(application);
        }
        return model;
    }

    private Model(Application application) {
        dao = ListsDatabase.getInstance(application).getListDao();
    }


    @Override
    public Flowable<List<UserList>> getAllLists() {
        return dao.getAllLists();
    }

    @Override
    public Flowable<List<Item>> getListContents(int listId) {
        return dao.getListContents(listId);
    }


    @Override
    public void addList(UserList list) {
        Timber.d("addList");
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
    public void changeListName(int listId, String newName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void changeItemName(int itemId, String newName) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void moveListPosition(int listId, int newPosition) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void moveItemPosition(int itemId, int newPosition) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void forceRefreshLists() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void forceRefreshListContents(int listId) {
        throw new UnsupportedOperationException();
    }
}
