package com.example.david.lists.common;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.example.david.lists.BuildConfig;
import com.example.david.lists.R;
import com.example.david.lists.common.buildlogic.AppComponent;
import com.example.david.lists.common.buildlogic.DaggerAppComponent;
import com.example.david.lists.util.UtilNightMode;

import io.fabric.sdk.android.Fabric;

abstract class ListsApplicationBase extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initAppComponent();
        init();
        firebaseAuthListener();
    }

    private void initAppComponent() {
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build();
    }

    /**
     * If the user signs-out, {@link AppComponent} needs to be re-created
     * otherwise the dependencies it creates will will still be tied
     * to the account the user just signed-out of.
     */
    private void firebaseAuthListener() {
        appComponent.userRepository().userSignedOutObservable().observeForever(signedOut -> {
            if (signedOut) {
                initAppComponent();
            }
        });
    }


    public AppComponent getAppComponent() {
        return this.appComponent;
    }


    private void init() {
        checkNetworkConnection();
        setNightMode();
        initFabric();
    }

    private void checkNetworkConnection() {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(getApplicationContext(), R.string.error_msg_no_network_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void setNightMode() {
        if (UtilNightMode.isNightModeEnabled(this)) {
            UtilNightMode.setNight(this);
        } else {
            UtilNightMode.setDay(this);
        }
    }

    private void initFabric() {
        // Initializes Fabric only for builds that are not of the debug build type.
        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);
    }
}
