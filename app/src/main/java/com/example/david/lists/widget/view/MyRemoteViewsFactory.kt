package com.example.david.lists.widget.view

import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.david.lists.R
import com.example.david.lists.common.subscribeFlowableItem
import com.example.david.lists.util.ISchedulerProviderContract
import com.example.david.lists.util.UtilExceptions
import com.example.domain.datamodel.Item
import com.example.domain.repository.IRepositoryContract
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class MyRemoteViewsFactory(private val packageName: String,
                           private val userListId: String,
                           private val repo: IRepositoryContract.Repository,
                           private val disposable: CompositeDisposable,
                           private val appWidgetManager: AppWidgetManager,
                           private val widgetId: Int,
                           private val schedulerProvider: ISchedulerProviderContract) : RemoteViewsService.RemoteViewsFactory {

    private val itemList: MutableList<Item> = ArrayList()

    override fun onCreate() {
        disposable.add(subscribeFlowableItem(
                repo.getItems(userListId),
                { onNextList(it) },
                { UtilExceptions.throwException(it) },
                schedulerProvider
        ))
    }

    private fun onNextList(list: List<Item>) {
        itemList.apply {
            clear()
            addAll(list)
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_list_view)
    }


    override fun getViewAt(position: Int) =
            RemoteViews(
                    packageName,
                    R.layout.widget_list_item
            ).apply {
                setTextViewText(
                        R.id.widget_list_item_tv_title,
                        itemList[position].title
                )
            }


    override fun onDataSetChanged() {}

    override fun getCount() = itemList.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getViewTypeCount() = 1

    override fun hasStableIds() = true

    override fun getLoadingView() = null

    override fun onDestroy() {
        disposable.clear()
    }
}
