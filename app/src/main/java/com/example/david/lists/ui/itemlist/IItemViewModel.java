package com.example.david.lists.ui.itemlist;

import androidx.lifecycle.LiveData;

import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Item;

import java.util.List;

public interface IItemViewModel {
    void addButtonClicked();

    void add(String title);

    void edit(Item item);

    void changeTitle(EditingInfo editingInfo, String newTitle);

    void dragging(IItemAdapter adapter, int fromPosition, int toPosition);

    void movedPermanently(int newPosition);

    void swipedLeft(IItemAdapter adapter, int position);

    void delete(IItemAdapter adapter, int position);

    void undoRecentDeletion(IItemAdapter adapter);

    void deletionNotificationTimedOut();


    LiveData<List<Item>> getItemList();

    LiveData<Boolean> getEventDisplayLoading();

    LiveData<String> getErrorMessage();

    LiveData<Boolean> getEventDisplayError();

    LiveData<String> getEventNotifyUserOfDeletion();

    LiveData<String> getEventAdd();

    LiveData<EditingInfo> getEventEdit();

    LiveData<Void> getEventFinish();
}
