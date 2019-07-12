package com.example.david.lists.view.itemlist;

import androidx.lifecycle.LiveData;

import com.example.david.lists.data.datamodel.Item;

import java.util.List;

public interface IItemViewContract {
    interface ViewModel {
        void addButtonClicked();

        void edit(Item item);

        void dragging(Adapter adapter, int fromPosition, int toPosition);

        void movedPermanently(int newPosition);

        void swipedLeft(Adapter adapter, int position);

        void delete(Adapter adapter, int position);

        void undoRecentDeletion(Adapter adapter);

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

    interface Adapter {
        void submitList(List<Item> list);

        void move(int fromPosition, int toPosition);

        void remove(int position);

        void reAdd(int position, Item item);
    }
}
