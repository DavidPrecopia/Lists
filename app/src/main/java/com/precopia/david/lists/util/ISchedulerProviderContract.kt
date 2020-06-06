package com.precopia.david.lists.util

import io.reactivex.rxjava3.core.Scheduler

interface ISchedulerProviderContract {
    fun io(): Scheduler

    fun ui(): Scheduler
}
