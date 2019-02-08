package com.example.david.lists.util;

import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UtilExceptionsTest {
    @Test(expected = RuntimeException.class)
    public void throwRuntimeExceptionTest() {
        UtilExceptions.throwException(new RuntimeException());
    }

    @Test
    public void throwThrowableExceptionTest() {
        Throwable throwableMock = mock(Throwable.class);
        UtilExceptions.throwException(throwableMock);
        verify(throwableMock, times(1)).printStackTrace();
    }
}