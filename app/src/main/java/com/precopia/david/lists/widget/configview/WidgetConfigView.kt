package com.precopia.david.lists.widget.configview

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.precopia.david.lists.R
import com.precopia.david.lists.widget.common.buildlogic.SHARED_PREFS
import com.precopia.david.lists.widget.configview.IWidgetConfigContract.LogicEvents
import com.precopia.david.lists.widget.configview.IWidgetConfigContract.ViewEvents
import com.precopia.david.lists.widget.configview.buildlogic.DaggerWidgetConfigComponent
import com.precopia.david.lists.widget.view.WidgetRemoteView
import com.precopia.domain.datamodel.UserList
import kotlinx.android.synthetic.main.widget_config_view.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class WidgetConfigView : AppCompatActivity(R.layout.widget_config_view),
        IWidgetConfigContract.View {

    @Inject
    lateinit var logic: IWidgetConfigContract.Logic

    @Inject
    @Named(SHARED_PREFS)
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var adapter: IWidgetConfigContract.Adapter
    @Inject
    lateinit var layoutManger: Provider<LinearLayoutManager>
    @Inject
    lateinit var dividerItemDecorator: RecyclerView.ItemDecoration


    private val widgetId: Int
        get() = intent.extras?.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
        )!!

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        initView()
        with(logic) {
            onEvent(LogicEvents.OnStart(widgetId))
            observe().observe(this@WidgetConfigView, Observer { evalViewEvents(it) })
        }
    }

    private fun inject() {
        DaggerWidgetConfigComponent.builder()
                .application(application)
                .view(this)
                .context(applicationContext)
                .build()
                .inject(this)
    }

    private fun evalViewEvents(event: ViewEvents) {
        when (event) {
            is ViewEvents.SetViewData -> setViewData(event.list)
            ViewEvents.SetStateDisplayList -> setStateDisplayList()
            ViewEvents.SetStateLoading -> setStateLoading()
            is ViewEvents.SetStateError -> setStateError(event.message)
            is ViewEvents.SetResults -> setResults(event.widgetId, event.resultCode)
            is ViewEvents.FinishView -> finishView(event.widgetId)
            ViewEvents.FinishViewInvalidId -> finishViewInvalidId()
            is ViewEvents.SaveDetails -> saveDetails(
                    event.id, event.title, event.sharedPrefKeyId, event.sharedPrefKeyTitle
            )
        }
    }


    private fun initView() {
        initRecyclerView()
        initToolbar()
    }

    private fun initRecyclerView() {
        recycler_view.apply {
            setHasFixedSize(true)
            layoutManager = layoutManger.get()
            addItemDecoration(dividerItemDecorator)
            adapter = (this@WidgetConfigView.adapter as RecyclerView.Adapter<*>)
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.title_widget_config_activity)
    }


    private fun setResults(widgetId: Int, resultCode: Int) {
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        setResult(resultCode, resultValue)
    }

    private fun saveDetails(id: String, title: String, sharedPrefKeyId: String, sharedPrefKeyTitle: String) {
        sharedPrefs.edit {
            putString(sharedPrefKeyId, id)
            putString(sharedPrefKeyTitle, title)
        }
    }

    private fun finishView(widgetId: Int) {
        updateWidget(widgetId)
        super.finish()
    }

    private fun updateWidget(widgetId: Int) {
        val remoteView = WidgetRemoteView(application, widgetId).updateWidget()
        AppWidgetManager.getInstance(application).updateAppWidget(widgetId, remoteView)
    }

    private fun finishViewInvalidId() {
        super.finish()
    }


    private fun setViewData(list: List<UserList>) {
        adapter.setData(list)
    }


    private fun setStateDisplayList() {
        progress_bar.isGone = true
        tv_error.isGone = true
        recycler_view.isVisible = true
    }

    private fun setStateLoading() {
        tv_error.isGone = true
        recycler_view.isGone = true
        progress_bar.isVisible = true
    }

    private fun setStateError(message: String) {
        recycler_view.isGone = true
        progress_bar.isGone = true

        tv_error.text = message
        tv_error.isVisible = true
    }
}