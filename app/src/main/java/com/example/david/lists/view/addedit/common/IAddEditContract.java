package com.example.david.lists.view.addedit.common;

public interface IAddEditContract {
    interface View {
        void setStateError(String message);

        void finishView();
    }

    interface Logic {
        String getCurrentTitle();

        void validateInput(String input);
    }

    interface ViewModel {
        void setTaskType(TaskType taskType);

        TaskType getCurrentTaskType();

        void setId(String id);

        void setCurrentTitle(String title);

        void setUserListId(String userListId);

        void setLastPosition(int position);

        String getId();

        String getCurrentTitle();

        String getUserListId();

        int getLastPosition();

        String getMsgEmptyTitle();

        String getMsgTitleUnchanged();
    }

    enum TaskType {
        TASK_ADD,
        TASK_EDIT
    }
}
