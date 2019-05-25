package com.example.david.lists.view.itemlist;

import androidx.lifecycle.LiveData;

import com.example.david.lists.data.datamodel.Item;

import java.util.List;

public interface IItemViewModel {
    void addButtonClicked();

    void edit(Item item);

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

    /**
     * @return the UserListId of this Item.
     */
    LiveData<String> getEventAdd();

    LiveData<Item> getEventEdit();

    LiveData<Void> getEventFinish();
}
