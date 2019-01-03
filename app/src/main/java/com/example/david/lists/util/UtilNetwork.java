package com.example.david.lists.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.CONNECTIVITY_SERVICE;

final class UtilNetwork {
    private UtilNetwork() {
    }

    static boolean haveNetworkConnection(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return networkInfo != null;
    }
}
