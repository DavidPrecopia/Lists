package com.precopia.david.lists.widget.configview

import androidx.lifecycle.LiveData
import com.precopia.domain.datamodel.UserList

interface IWidgetConfigContract {
    interface View

    interface Adapter {
        fun setData(list: List<UserList>)
    }

    interface Logic {
        fun onEvent(event: LogicEvents)

        fun observe(): LiveData<ViewEvents>
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


    sealed class ViewEvents {
        data class SetViewData(val list: List<UserList>) : ViewEvents()
        object SetStateDisplayList : ViewEvents()
        object SetStateLoading : ViewEvents()
        data class SetStateError(val message: String) : ViewEvents()
        data class SetResults(val widgetId: Int, val resultCode: Int) : ViewEvents()
        data class FinishView(val widgetId: Int) : ViewEvents()
        object FinishViewInvalidId : ViewEvents()
        data class SaveDetails(
                val id: String, val title: String, val sharedPrefKeyId: String, val sharedPrefKeyTitle: String
        ) : ViewEvents()
    }

    sealed class LogicEvents {
        data class OnStart(val widgetId: Int) : LogicEvents()
        data class SelectedUserList(val position: Int) : LogicEvents()
    }
}
