package com.example.david.lists.common

import com.example.david.lists.util.ISchedulerProviderContract
import com.example.domain.datamodel.Item
import com.example.domain.datamodel.UserList
import io.reactivex.Completable
import io.reactivex.Flowable

internal fun <E : List<UserList>> subscribeFlowableUserList(flowable: Flowable<E>,
                                                            onNext: (E) -> Unit,
                                                            onError: (t: Throwable) -> Unit,
                                                            schedulerProvider: ISchedulerProviderContract) =
        flowable
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribe({ onNext.invoke(it) }, { onError.invoke(it) })

internal fun <E : List<Item>> subscribeFlowableItem(flowable: Flowable<E>,
                                                    onNext: (E) -> Unit,
                                                    onError: (t: Throwable) -> Unit,
                                                    schedulerProvider: ISchedulerProviderContract) =
        flowable
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribe({ onNext.invoke(it) }, { onError.invoke(it) })


internal fun subscribeCompletable(completable: Completable,
                                  onComplete: () -> Unit,
                                  onError: (t: Throwable) -> Unit,
                                  schedulerProvider: ISchedulerProviderContract) =
        completable
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribe({ onComplete.invoke() }, { onError.invoke(it) })