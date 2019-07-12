package com.example.david.lists.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

public final class UserRepository implements IRepositoryContract.UserRepository {

    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<Boolean> userSignedOut;

    public UserRepository(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
        userSignedOut = new MutableLiveData<>();
        init();
    }

    private void init() {
        firebaseAuth.addAuthStateListener(firebaseAuth1 -> {
            if (this.signedOut()) {
                userSignedOut.setValue(true);
            }
        });
    }

    @Override
    public boolean isAnonymous() {
        return firebaseAuth.getCurrentUser().isAnonymous();
    }

    @Override
    public boolean signedOut() {
        return firebaseAuth.getCurrentUser() == null;
    }

    @Override
    public LiveData<Boolean> userSignedOutObservable() {
        return userSignedOut;
    }
}
