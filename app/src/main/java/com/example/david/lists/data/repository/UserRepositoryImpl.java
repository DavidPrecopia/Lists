package com.example.david.lists.data.repository;

import com.google.firebase.auth.FirebaseAuth;

public final class UserRepositoryImpl implements IUserRepository {

    private final FirebaseAuth firebaseAuth;

    public UserRepositoryImpl(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public boolean isAnonymous() {
        return firebaseAuth.getCurrentUser().isAnonymous();
    }

    @Override
    public boolean signedOut() {
        return firebaseAuth.getCurrentUser() == null;
    }
}
