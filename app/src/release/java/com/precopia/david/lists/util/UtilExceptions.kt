package com.precopia.david.lists.util

import com.google.firebase.crashlytics.FirebaseCrashlytics

object UtilExceptions {
    fun throwException(runtimeException: RuntimeException) {
        FirebaseCrashlytics.getInstance().recordException(runtimeException)
    }

    fun throwException(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}
