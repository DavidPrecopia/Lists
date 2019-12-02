package com.example.androiddata.common

import io.reactivex.Completable
import io.reactivex.CompletableEmitter
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter

internal fun createCompletable(function: (emitter: CompletableEmitter) -> Unit) =
        Completable.create(function)

internal fun <L : List<Any>> createFlowable(function: (emitter: FlowableEmitter<L>) -> Unit) =
        Flowable.create(function, io.reactivex.BackpressureStrategy.BUFFER)