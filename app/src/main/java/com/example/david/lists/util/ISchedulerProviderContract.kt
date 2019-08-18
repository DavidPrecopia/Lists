package com.example.david.lists.util

import io.reactivex.Scheduler

interface ISchedulerProviderContract {
    fun io(): Scheduler

    fun ui(): Scheduler
}
