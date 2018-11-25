package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.datamodel.Item;

import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.Flowable;

public interface IRemoteStorageContract {
    Flowable<List<Group>> getGroups();

    Flowable<List<Item>> getItems(String groupId);

    void addGroup(Group group);

    void addItem(Item item);

    void deleteGroups(List<Group> group);

    void deleteItems(List<Item> item);

    void renameGroup(String groupId, String newName);

    void renameItem(String itemId, String newName);

    void updateGroupPositionsIncrement(Group group, int oldPosition, int newPosition);

    void updateGroupPositionsDecrement(Group group, int oldPosition, int newPosition);

    void updateItemPositionsIncrement(Item item, int oldPosition, int newPosition);

    void updateItemPositionsDecrement(Item item, int oldPosition, int newPosition);

    LiveData<List<Group>> getEventGroupDeleted();
}
