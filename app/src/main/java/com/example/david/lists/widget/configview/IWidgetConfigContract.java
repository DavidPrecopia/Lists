package com.example.david.lists.widget.configview;

import com.example.david.lists.data.datamodel.UserList;

import java.util.List;

public interface IWidgetConfigContract {
    interface View {
        void setData(List<UserList> list);

        void setStateDisplayList();

        void setStateLoading();

        void setStateError(String message);

        void setResults(int widgetId, int resultCode);

        void finishView(int widgetId);

        void finishViewInvalidId();
    }

    interface Adapter {
        void setData(List<UserList> list);
    }

    interface Logic {
        void onStart(int appWidgetId);

        void selectedUserList(UserList userList);

        void onDestroy();
    }

    interface ViewModel {
        void setViewData(List<UserList> list);

        List<UserList> getViewData();

        void setWidgetId(int widgetId);

        int getWidgetId();

        int getInvalidWidgetId();

        int getResultOk();

        int getResultCancelled();

        String getErrorMsg();

        String getErrorMsgEmptyList();

        String getSharedPrefKeyId();

        String getSharedPrefKeyTitle();
    }
}
