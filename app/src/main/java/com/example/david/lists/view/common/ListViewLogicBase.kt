package com.example.david.lists.view.common

import com.example.david.lists.util.ISchedulerProviderContract
import com.example.domain.repository.IRepositoryContract
import io.reactivex.disposables.CompositeDisposable

abstract class ListViewLogicBase protected constructor(protected val repo: IRepositoryContract.Repository,
                                                       protected val schedulerProvider: ISchedulerProviderContract,
                                                       protected val disposable: CompositeDisposable)
