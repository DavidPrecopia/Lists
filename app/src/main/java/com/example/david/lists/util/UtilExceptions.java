package com.example.david.lists.util;

import com.crashlytics.android.Crashlytics;
import com.example.david.lists.BuildConfig;

public final class UtilExceptions {
    private UtilExceptions() {
    }

    public static void throwException(RuntimeException runtimeException) {
        if (BuildConfig.DEBUG) {
            throw runtimeException;
        } else {
            Crashlytics.logException(runtimeException);
        }
    }

    public static void throwException(Throwable throwable) {
        if (BuildConfig.DEBUG) {
            try {
                throw throwable;
            } catch (Throwable throwable1) {
                throwable1.printStackTrace();
            }
        } else {
            Crashlytics.logException(throwable);
        }
    }
}
