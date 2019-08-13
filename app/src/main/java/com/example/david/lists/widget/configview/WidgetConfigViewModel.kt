package com.example.david.lists.widget.configview

import android.app.Activity
import android.app.Application
import android.appwidget.AppWidgetManager
import com.example.david.lists.R
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.view.common.ViewModelBase
import com.example.david.lists.widget.UtilWidgetKeys
import java.util.*

class WidgetConfigViewModel(application: Application) :
        ViewModelBase(application),
        IWidgetConfigContract.ViewModel {

    override var viewData: List<UserList> = ArrayList()

    override var widgetId: Int = 0

    override val invalidWidgetId: Int
        get() = AppWidgetManager.INVALID_APPWIDGET_ID


    override val resultOk: Int
        get() = Activity.RESULT_OK

    override val resultCancelled: Int
        get() = Activity.RESULT_CANCELED


    override val errorMsg: String
        get() = getStringRes(R.string.error_msg_generic)

    override val errorMsgEmptyList: String
        get() = getStringRes(R.string.error_msg_empty_user_list)


    override val sharedPrefKeyId: String
        get() = UtilWidgetKeys.getSharedPrefKeyId(application, this.widgetId)

    override val sharedPrefKeyTitle: String
        get() = UtilWidgetKeys.getSharedPrefKeyTitle(application, this.widgetId)
}
