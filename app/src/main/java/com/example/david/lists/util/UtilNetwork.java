package com.example.david.lists.util;

import android.net.NetworkInfo;

public class UtilNetwork {
    private UtilNetwork() {
    }

    public static boolean notConnected(NetworkInfo networkInfo) {
        return networkInfo == null;
    }
}
