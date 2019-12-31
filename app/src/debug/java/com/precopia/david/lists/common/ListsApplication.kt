package com.precopia.david.lists.common

import timber.log.Timber

internal class ListsApplication : ListsApplicationBase() {
    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}