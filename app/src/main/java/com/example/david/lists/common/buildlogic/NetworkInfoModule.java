package com.example.david.lists.common.buildlogic;

import android.app.Application;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.annotation.Nullable;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.CONNECTIVITY_SERVICE;

@Module
class NetworkInfoModule {
    @Nullable
    @Provides
    NetworkInfo networkInfo(Application application) {
        return ((ConnectivityManager) application
                .getSystemService(CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
    }
}
