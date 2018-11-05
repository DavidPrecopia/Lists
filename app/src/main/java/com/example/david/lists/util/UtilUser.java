package com.example.david.lists.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UtilUser {

    private static final long ONE_MINUTES = 6000;

    private UtilUser() {
    }

    public static boolean isAnonymous() {
        return FirebaseAuth.getInstance().getCurrentUser().isAnonymous();
    }

    public static String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static boolean signedOut() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }

    public static boolean recentlySignedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return false;
        long timeSeparation = Math.abs(
                user.getMetadata().getLastSignInTimestamp() - System.currentTimeMillis()
        );
        return timeSeparation <= ONE_MINUTES;
    }
}
