package com.example.david.lists.util;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

public final class UtilRxJava {
    private UtilRxJava() {
    }

    public static void completableIoAccess(Completable completable) {
        completable
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
