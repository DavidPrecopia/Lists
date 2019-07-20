package com.example.david.lists.view.common;

import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.util.ISchedulerProviderContract;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ListViewLogicBase {

    protected final IRepositoryContract.Repository repo;
    protected final ISchedulerProviderContract schedulerProvider;
    protected final CompositeDisposable disposable;

    protected ListViewLogicBase(IRepositoryContract.Repository repo,
                                ISchedulerProviderContract schedulerProvider,
                                CompositeDisposable disposable) {
        this.repo = repo;
        this.schedulerProvider = schedulerProvider;
        this.disposable = disposable;
    }
}
