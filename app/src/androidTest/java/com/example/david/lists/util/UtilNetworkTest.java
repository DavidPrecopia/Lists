package com.example.david.lists.util;

import android.net.NetworkInfo;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UtilNetworkTest {

    /**
     * A non-null {@link NetworkInfo} instance means their is a
     * network connection.
     * I am mocking {@link NetworkInfo} to ensure it is non-null
     * and avoid flakiness.
     */
    @Test
    public void notConnectedReturnsFalse() {
        NetworkInfo networkInfo = mock(NetworkInfo.class);

        assertThat(
                UtilNetwork.notConnected(networkInfo),
                is(false)
        );
    }

    /**
     * A null {@link NetworkInfo} means their is
     * no network connection.
     */
    @Test
    public void notConnectedReturnsTrue() {
        assertThat(
                UtilNetwork.notConnected(null),
                is(true)
        );
    }
}