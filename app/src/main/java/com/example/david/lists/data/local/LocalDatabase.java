package com.example.david.lists.data.local;

import android.app.Application;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserList.class, Item.class}, version = 1, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {

    private static volatile LocalDatabase instance;

    public static LocalDatabase getInstance(Application application) {
        if (instance == null) {
            synchronized (LocalDatabase.class) {
                instance = Room.databaseBuilder(
                        application, LocalDatabase.class, LocalDatabaseConstants.DATABASE_NAME
                ).build();
            }
        }
        return instance;
    }

    public abstract LocalDao getLocalDao();
}