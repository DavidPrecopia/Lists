package com.example.androiddata.common

/**
 * PLACEHOLDER unit I find a superior solution.
 */
internal object UtilExceptions {
    fun throwException(runtimeException: RuntimeException) {
        throw runtimeException
    }

    fun throwException(throwable: Throwable) {
        try {
            throw throwable
        } catch (throwable1: Throwable) {
            throwable1.printStackTrace()
        }
    }
}
