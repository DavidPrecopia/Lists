package com.precopia.david.lists

import com.precopia.david.lists.util.ISchedulerProviderContract
import io.mockk.every
import io.reactivex.schedulers.Schedulers

/**
 * Keeping code DRY.
 *
 * Trampoline: a Scheduler that queues work on the current thread to be
 * executed after the current work completes.
 * Another way to put it: emits result in a sequentially predictable order.
 *
 * Because this is a unit test that is running on the JVM,
 * all operations run on the same thread the tests are running on.
 * Otherwise, an error in thrown by the Observable.
 */
object SchedulerProviderMockInit {
    fun init(schedulerProvider: ISchedulerProviderContract) {
        every { schedulerProvider.io() } returns Schedulers.trampoline()
        every { schedulerProvider.ui() } returns Schedulers.trampoline()
    }
}
