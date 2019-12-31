package com.precopia.david.lists.widget.configview

import android.app.Activity
import android.appwidget.AppWidgetManager
import com.precopia.david.lists.R
import com.precopia.david.lists.widget.common.UtilWidgetKeys
import com.precopia.domain.datamodel.UserList
import java.util.*

class WidgetConfigViewModel(private val utilWidgetKeys: UtilWidgetKeys,
                            private val getStringRes: (Int) -> String) :
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
        get() = utilWidgetKeys.getSharedPrefKeyId(this.widgetId)

    override val sharedPrefKeyTitle: String
        get() = utilWidgetKeys.getSharedPrefKeyTitle(this.widgetId)
}
