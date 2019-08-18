package com.example.david.lists.util

import com.crashlytics.android.Crashlytics
import com.example.david.lists.BuildConfig

object UtilExceptions {
    fun throwException(runtimeException: RuntimeException) {
        if (BuildConfig.DEBUG) {
            throw runtimeException
        } else {
            Crashlytics.logException(runtimeException)
        }
    }

    fun throwException(throwable: Throwable) {
        if (BuildConfig.DEBUG) {
            try {
                throw throwable
            } catch (throwable1: Throwable) {
                throwable1.printStackTrace()
            }
        } else {
            Crashlytics.logException(throwable)
        }
    }
}
