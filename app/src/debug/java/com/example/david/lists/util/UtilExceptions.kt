package com.example.david.lists.util

object UtilExceptions {
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
