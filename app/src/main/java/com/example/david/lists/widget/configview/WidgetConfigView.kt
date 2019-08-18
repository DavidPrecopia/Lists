package com.example.david.lists.widget.configview

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.david.lists.R
import com.example.david.lists.data.datamodel.UserList
import com.example.david.lists.widget.buildlogic.SharedPrefsModule.Companion.SHARED_PREFS
import com.example.david.lists.widget.configview.buildlogic.DaggerWidgetConfigViewComponent
import com.example.david.lists.widget.view.WidgetRemoteView
import kotlinx.android.synthetic.main.widget_config_view.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

class WidgetConfigView : AppCompatActivity(), IWidgetConfigContract.View {

    @Inject
    lateinit var logic: IWidgetConfigContract.Logic

    @Inject
    @field:Named(SHARED_PREFS)
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var adapter: IWidgetConfigContract.Adapter
    @Inject
    lateinit var layoutManger: Provider<LinearLayoutManager>
    @Inject
    lateinit var dividerItemDecorator: RecyclerView.ItemDecoration


    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_config_view)
        initView()

        logic.onStart()
    }

    private fun inject() {
        DaggerWidgetConfigViewComponent.builder()
                .application(application)
                .context(applicationContext)
                .view(this)
                .intent(intent)
                .build()
                .inject(this)
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
            adapter = this@WidgetConfigView.adapter as RecyclerView.Adapter<*>
        }
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle(R.string.title_widget_config_activity)
    }


    override fun setResults(widgetId: Int, resultCode: Int) {
        val resultValue = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        }
        setResult(resultCode, resultValue)
    }

    override fun saveDetails(id: String, title: String, sharedPrefKeyId: String, sharedPrefKeyTitle: String) {
        sharedPrefs.edit {
            putString(sharedPrefKeyId, id)
            putString(sharedPrefKeyTitle, title)
        }
    }

    override fun finishView(widgetId: Int) {
        updateWidget(widgetId)
        super.finish()
    }

    private fun updateWidget(widgetId: Int) {
        val remoteView = WidgetRemoteView(application, widgetId).updateWidget()
        AppWidgetManager.getInstance(application).updateAppWidget(widgetId, remoteView)
    }

    override fun finishViewInvalidId() {
        super.finish()
    }


    override fun setViewData(list: List<UserList>) {
        adapter.setData(list)
    }


    override fun setStateDisplayList() {
        progress_bar.isGone = true
        tv_error.isGone = true
        recycler_view.isVisible = true
    }

    override fun setStateLoading() {
        tv_error.isGone = true
        recycler_view.isGone = true
        progress_bar.isVisible = true
    }

    override fun setStateError(message: String) {
        recycler_view.isGone = true
        progress_bar.isGone = true

        tv_error.text = message
        tv_error.isVisible = true
    }


    override fun onDestroy() {
        logic.onDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        logic.onDestroy()
        super.onBackPressed()
    }
}