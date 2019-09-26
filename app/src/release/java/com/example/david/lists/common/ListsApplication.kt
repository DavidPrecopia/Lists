package com.example.david.lists.common

import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

internal class ListsApplication : ListsApplicationBase() {
    override fun onCreate() {
        super.onCreate()
        initCrashlytics()
    }

    private fun initCrashlytics() {
        Fabric.with(this, Crashlytics())
    }
}
