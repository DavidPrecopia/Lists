package com.example.david.lists.util;

import io.reactivex.Scheduler;

public interface ISchedulerProviderContract {
    Scheduler io();

    Scheduler ui();
}
