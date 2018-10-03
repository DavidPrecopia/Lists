package com.example.david.lists.database;

import android.app.Application;

import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserList.class, Item.class}, version = 1, exportSchema = false)
public abstract class ListsDatabase extends RoomDatabase {

    private static volatile ListsDatabase instance;

    public static ListsDatabase getInstance(Application application) {
        if (instance == null) {
            synchronized (ListsDatabase.class) {
                instance = Room.databaseBuilder(
                        application, ListsDatabase.class, DatabaseContract.DATABASE_NAME
                ).build();
            }
        }
        return instance;
    }

    public abstract ListsDao getListDao();
}
