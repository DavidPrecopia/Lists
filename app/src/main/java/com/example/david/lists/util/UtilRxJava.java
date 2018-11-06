package com.example.david.lists.util;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public final class UtilRxJava {
    private UtilRxJava() {
    }

    public static void completableIoAccess(Completable completable) {
        completable
                .doOnError(Timber::e)
                .subscribeOn(Schedulers.io())
                .subscribe();
    }
}
