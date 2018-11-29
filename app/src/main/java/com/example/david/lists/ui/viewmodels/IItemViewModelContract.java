package com.example.david.lists.ui.viewmodels;

import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.ui.adapaters.IItemAdapterContract;

import java.util.List;

import androidx.lifecycle.LiveData;

public interface IItemViewModelContract {
    void addButtonClicked();

    void add(String title);

    void edit(int position);

    void changeTitle(EditingInfo editingInfo, String newTitle);

    void dragging(IItemAdapterContract adapter, int fromPosition, int toPosition);

    void movedPermanently(IItemAdapterContract adapter, int newPosition);

    void swipedLeft(IItemAdapterContract adapter, int position);

    void delete(IItemAdapterContract adapter, int position);

    void undoRecentDeletion(IItemAdapterContract adapter);

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
