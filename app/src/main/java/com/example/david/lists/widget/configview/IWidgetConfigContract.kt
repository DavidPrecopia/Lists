package com.example.david.lists.widget.configview

import com.example.domain.datamodel.UserList

interface IWidgetConfigContract {
    interface View {
        fun setViewData(list: List<UserList>)

        fun setStateDisplayList()

        fun setStateLoading()

        fun setStateError(message: String)

        fun setResults(widgetId: Int, resultCode: Int)

        fun saveDetails(id: String, title: String, sharedPrefKeyId: String, sharedPrefKeyTitle: String)

        fun finishView(widgetId: Int)

        fun finishViewInvalidId()
    }

    interface Adapter {
        fun setData(list: List<UserList>)
    }

    interface Logic {
        fun onStart(widgetId: Int)

        fun selectedUserList(position: Int)

        fun onDestroy()
    }

    interface ViewModel {
        var viewData: List<UserList>

        var widgetId: Int

        val invalidWidgetId: Int

        val resultOk: Int

        val resultCancelled: Int

        val errorMsg: String

        val errorMsgEmptyList: String

        val sharedPrefKeyId: String

        val sharedPrefKeyTitle: String
    }
}
