package com.example.david.lists.data.model;

import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.datamodel.Item;

import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.Flowable;

public interface IModelContract {
    Flowable<List<Group>> getAllGroups();

    Flowable<List<Item>> getGroupItems(String groupId);

    void addGroup(Group group);

    void addItem(Item item);

    void deleteGroups(List<Group> groups);

    void deleteItems(List<Item> items);

    void renameGroup(String groupId, String newName);

    void renameItem(String itemId, String newName);

    void updateGroupPosition(Group group, int oldPosition, int newPosition);

    void updateItemPosition(Item item, int oldPosition, int newPosition);

    LiveData<List<Group>> getEventGroupDeleted();
}
