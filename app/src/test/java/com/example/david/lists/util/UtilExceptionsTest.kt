package com.example.david.lists.util

import org.junit.Test
import org.mockito.Mockito.*

class UtilExceptionsTest {
    @Test(expected = RuntimeException::class)
    fun throwRuntimeExceptionTest() {
        UtilExceptions.throwException(RuntimeException())
    }

    @Test
    fun throwThrowableExceptionTest() {
        val throwableMock = mock(Throwable::class.java)
        UtilExceptions.throwException(throwableMock)
        verify(throwableMock, times(1)).printStackTrace()
    }
}