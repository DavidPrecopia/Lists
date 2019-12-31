package com.precopia.david.lists.util

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UtilExceptionsTest {
    @Test
    fun `Throw RuntimeException`() {
        assertThrows<java.lang.RuntimeException> {
            UtilExceptions.throwException(RuntimeException())
        }
    }

    @Test
    fun `Throw ThrowableException`() {
        val throwableMock = mockk<Throwable>(relaxed = true)

        UtilExceptions.throwException(throwableMock)

        verify(atMost = 1) { throwableMock.printStackTrace() }
    }
}