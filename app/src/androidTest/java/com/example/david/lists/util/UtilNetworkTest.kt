package com.example.david.lists.util

import android.net.NetworkInfo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UtilNetworkTest {

    /**
     * A non-null [NetworkInfo] indicates that their is a
     * network connection.
     *
     * I am mocking [NetworkInfo] to ensure it is non-null.
     */
    @Test
    fun notConnectedReturnsFalse() {
        val networkInfo = mock(NetworkInfo::class.java)

        assertThat(
                UtilNetwork.notConnected(networkInfo),
                `is`(false)
        )
    }

    /**
     * A null [NetworkInfo] indicates that their is
     * no network connection.
     */
    @Test
    fun notConnectedReturnsTrue() {
        assertThat(
                UtilNetwork.notConnected(null),
                `is`(true)
        )
    }
}