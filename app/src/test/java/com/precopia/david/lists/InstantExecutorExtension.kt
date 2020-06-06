package com.precopia.david.lists

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * This class was copied from:
 * https://jeroenmols.com/blog/2019/01/17/livedatajunit5/
 *
 * This tricks LiveData into thinking that it is running on Android's main thread.
 *
 * Because I am using JUnit5, I cannot simply apply a rule to the test class as
 * you can with JUnit4.
 */
class InstantExecutorExtension: BeforeEachCallback, AfterEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance()
                .setDelegate(object: TaskExecutor() {
                    override fun executeOnDiskIO(runnable: Runnable) = runnable.run()

                    override fun postToMainThread(runnable: Runnable) = runnable.run()

                    override fun isMainThread(): Boolean = true
                })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}