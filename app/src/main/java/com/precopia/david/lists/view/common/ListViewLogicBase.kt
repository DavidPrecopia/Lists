package com.precopia.david.lists.view.common

import com.precopia.david.lists.util.ISchedulerProviderContract
import com.precopia.domain.repository.IRepositoryContract
import io.reactivex.disposables.CompositeDisposable

abstract class ListViewLogicBase protected constructor(protected val repo: IRepositoryContract.Repository,
                                                       protected val schedulerProvider: ISchedulerProviderContract,
                                                       protected val disposable: CompositeDisposable)
