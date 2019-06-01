package com.example.david.lists.data.repository;

import androidx.lifecycle.LiveData;

public interface IUserRepository {
    boolean isAnonymous();

    boolean signedOut();

    LiveData<Boolean> userSignedOutObservable();
}
