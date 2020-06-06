package com.precopia.david.lists.common

import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.domain.constants.PhoneNumValidationResults
import com.precopia.domain.datamodel.Item
import com.precopia.domain.datamodel.UserList
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

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


internal fun <E : PhoneNumValidationResults> subscribeSingleValidatePhoneNum(single: Single<E>,
                                                                             onSuccess: (result: PhoneNumValidationResults) -> Unit,
                                                                             onError: (t: Throwable) -> Unit,
                                                                             schedulerProvider: ISchedulerProviderContract) =
        single
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .onTerminateDetach()
                .subscribe({ onSuccess.invoke(it) }, { onError.invoke(it) })