package com.example.david.lists.view.itemlist;

import com.example.david.lists.data.datamodel.Item;

import java.util.List;

public interface IItemViewContract {
    interface View {
        void openAddDialog(String userListId, int position);

        void openEditDialog(Item item);

        void submitList(List<Item> viewData);

        void notifyUserOfDeletion(String message);

        void setStateDisplayList();

        void setStateLoading();

        void setStateError(String message);

        void showMessage(String message);

        void finishView();
    }

    interface Logic {
        void onStart();

        void add();

        void edit(int position);

        void dragging(int fromPosition, int toPosition, IItemViewContract.Adapter adapter);

        void movedPermanently(int newPosition);

        void delete(int position, IItemViewContract.Adapter adapter);

        void undoRecentDeletion(IItemViewContract.Adapter adapter);

        void deletionNotificationTimedOut();

        void onDestroy();
    }

    interface ViewModel {
        String getUserListId();

        void setViewData(List<Item> items);

        List<Item> getViewData();

        void setTempPosition(int position);

        List<Item> getTempList();

        int getTempPosition();

        String getMsgListDeleted(String title);

        String getMsgItemDeleted();

        String getErrorMsg();

        String getErrorMsgEmptyList();

        String getErrorMsgInvalidUndo();
    }

    interface Adapter {
        void submitList(List<Item> list);

        void move(int fromPosition, int toPosition);

        void remove(int position);

        void reAdd(int position, Item item);
    }
}
