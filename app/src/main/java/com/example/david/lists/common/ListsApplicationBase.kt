package com.example.david.lists.common

import android.app.Application

import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.example.david.lists.BuildConfig
import com.example.david.lists.R
import com.example.david.lists.common.buildlogic.AppComponent
import com.example.david.lists.common.buildlogic.DaggerAppComponent
import com.example.david.lists.util.UtilNetwork
import com.example.david.lists.util.UtilNightMode

import io.fabric.sdk.android.Fabric
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
        checkNetworkConnection()
        setNightMode()
        initFabric()
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

    private fun initFabric() {
        // Initializes Fabric only for builds that are not of the debug build type.
        val crashlyticsKit = Crashlytics.Builder()
                .core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build()
        Fabric.with(this, crashlyticsKit)
    }
}