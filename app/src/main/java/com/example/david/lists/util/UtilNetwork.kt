package com.example.david.lists.util

import android.net.NetworkInfo

object UtilNetwork {
    fun notConnected(networkInfo: NetworkInfo?) = networkInfo == null
}
