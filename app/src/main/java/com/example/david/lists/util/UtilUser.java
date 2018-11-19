package com.example.david.lists.util;

import com.google.firebase.auth.FirebaseAuth;

public class UtilUser {

    private UtilUser() {
    }

    public static boolean isAnonymous() {
        return FirebaseAuth.getInstance().getCurrentUser().isAnonymous();
    }

    public static boolean signedOut() {
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }
}
