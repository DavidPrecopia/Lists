package com.example.david.lists.util

import com.crashlytics.android.Crashlytics

object UtilExceptions {
    fun throwException(runtimeException: RuntimeException) {
        Crashlytics.logException(runtimeException)
    }

    fun throwException(throwable: Throwable) {
        Crashlytics.logException(throwable)
    }
}
