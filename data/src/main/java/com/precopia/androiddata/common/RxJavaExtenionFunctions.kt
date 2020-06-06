package com.precopia.androiddata.common

import io.reactivex.rxjava3.core.*

internal fun createCompletable(function: (emitter: CompletableEmitter) -> Unit) =
        Completable.create(function)

internal fun <L : List<Any>> createFlowable(function: (emitter: FlowableEmitter<L>) -> Unit) =
        Flowable.create(function, BackpressureStrategy.BUFFER)