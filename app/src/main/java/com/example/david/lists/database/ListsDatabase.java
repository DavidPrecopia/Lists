package com.example.david.lists.database;

import android.app.Application;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;

import com.example.david.lists.datamodel.Item;
import com.example.david.lists.datamodel.UserList;

@Database(entities = {UserList.class, Item.class}, version = 1, exportSchema = false)
public abstract class ListsDatabase extends RoomDatabase {

    private static ListsDatabase database;

    public static ListsDatabase getInstnace(Application application) {
        if (database == null) {
            database = Room.databaseBuilder(
                    application, ListsDatabase.class, DatabaseContract.DATABASE_NAME
            ).build();
        }
        return database;
    }

    public abstract ListsDao getListDao();
}
