package com.example.david.lists;

import com.example.david.lists.util.ISchedulerProviderContract;

import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.when;

/**
 * Keeping code DRY.
 * <p>
 * Trampoline: a Scheduler that queues work on the current thread to be
 * executed after the current work completes.
 * Another way to put it: emits result in a sequentially predictable order.
 * <p>
 * Because this is a unit test that is running on the JVM,
 * all operations run on the same thread the tests are running on.
 * Otherwise, an error in thrown by the Observable.
 */
public class SchedulerProviderMockInit {
    private SchedulerProviderMockInit() {
    }

    public static void init(ISchedulerProviderContract schedulerProvider) {
        when(schedulerProvider.io()).thenReturn(Schedulers.trampoline());
        when(schedulerProvider.ui()).thenReturn(Schedulers.trampoline());
    }
}
