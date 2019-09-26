package com.example.david.lists.common

import android.app.Application
import android.os.Looper
import com.example.david.lists.R
import com.example.david.lists.common.buildlogic.AppComponent
import com.example.david.lists.common.buildlogic.DaggerAppComponent
import com.example.david.lists.util.UtilNetwork
import com.example.david.lists.util.UtilNightMode
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.longToast

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
    private fun firebaseAuthListener() {
        appComponent.userRepo().userSignedOutObservable().observeForever { signedOut ->
            if (signedOut!!) {
                initAppComponent()
            }
        }
    }


    private fun init() {
        initRxAndroidSchedulers()
        checkNetworkConnection()
        setNightMode()
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

    private fun checkNetworkConnection() {
        if (UtilNetwork.notConnected(appComponent.networkInfo())) {
            longToast(R.string.error_msg_no_network_connection)
        }
    }

    private fun setNightMode() {
        val utilNightMode = UtilNightMode(this)

        if (utilNightMode.nightModeEnabled) {
            utilNightMode.setNight()
        } else {
            utilNightMode.setDay()
        }
    }
}
