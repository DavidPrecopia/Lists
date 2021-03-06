package com.precopia.david.lists.common

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import com.precopia.david.lists.common.buildlogic.AppComponent
import com.precopia.david.lists.common.buildlogic.DaggerAppComponent
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers

internal abstract class ListsApplicationBase : Application() {

    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
        init()
        firebaseAuthListener()
    }

    private fun initAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build()
    }

    /**
     * If the user signs-out, [AppComponent] needs to be re-created
     * otherwise the dependencies it creates will will still be tied
     * to the account the user just signed-out of.
     */
    @SuppressLint("CheckResult")
    private fun firebaseAuthListener() {
        appComponent.userRepo().userSignedOutObservable().subscribe { signedOut ->
            if (signedOut!!) {
                initAppComponent()
            }
        }
    }


    private fun init() {
        initRxAndroidSchedulers()
        setTheme()
    }

    /**
     * This means events flowing to the main thread do not
     * have to wait for vsync, decreasing the likelihood of frame drops.
     * https://twitter.com/jakewharton/status/1170437658776133636?s=12
     */
    private fun initRxAndroidSchedulers() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler {
            AndroidSchedulers.from(Looper.getMainLooper(), true)
        }
    }

    private fun setTheme() {
        appComponent.utilTheme().restore()
    }
}
