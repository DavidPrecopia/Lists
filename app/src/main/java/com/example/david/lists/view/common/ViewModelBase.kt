package com.example.david.lists.view.common

import android.app.Application

abstract class ViewModelBase protected constructor(protected val application: Application) {
    protected fun getStringRes(resId: Int): String {
        return application.getString(resId)
    }

    protected fun getStringRes(resId: Int, any: Any): String {
        return application.getString(resId, any)
    }
}
