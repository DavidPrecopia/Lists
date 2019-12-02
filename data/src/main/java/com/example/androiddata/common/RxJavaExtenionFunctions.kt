package com.example.androiddata.common

import io.reactivex.Completable.create
import io.reactivex.CompletableEmitter

internal fun createCompletable(function: (emitter: CompletableEmitter) -> Unit) = create(function)