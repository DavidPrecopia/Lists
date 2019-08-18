package com.example.david.lists.widget.view

import android.appwidget.AppWidgetManager
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.example.david.lists.R
import com.example.david.lists.data.datamodel.Item
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.util.UtilExceptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import java.util.*

class MyRemoteViewsFactory(private val packageName: String,
                           private val userListId: String,
                           private val repo: IRepositoryContract.Repository,
                           private val disposable: CompositeDisposable,
                           private val appWidgetManager: AppWidgetManager,
                           private val widgetId: Int) : RemoteViewsService.RemoteViewsFactory {

    private val itemList: MutableList<Item> = ArrayList()

    override fun onCreate() {
        disposable.add(repo.getItems(userListId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(itemListObserver())
        )
    }

    private fun itemListObserver() = object : DisposableSubscriber<List<Item>>() {
        override fun onNext(newItemList: List<Item>) {
            itemList.apply {
                clear()
                addAll(newItemList)
            }
            appWidgetManager.notifyAppWidgetViewDataChanged(widgetId, R.id.widget_list_view)
        }

        override fun onError(t: Throwable) {
            UtilExceptions.throwException(t)
        }

        override fun onComplete() {

        }
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
