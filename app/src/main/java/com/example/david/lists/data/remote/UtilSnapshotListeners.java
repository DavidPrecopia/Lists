package com.example.david.lists.data.remote;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IUserRepository;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilExceptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;

import static com.example.david.lists.data.remote.RemoteRepositoryConstants.FIELD_ITEM_LIST_ID;
import static com.example.david.lists.data.remote.RemoteRepositoryConstants.FIELD_POSITION;

public final class UtilSnapshotListeners {

    private final Flowable<List<UserList>> userListFlowable;
    private final CollectionReference userListCollection;

    private final CollectionReference itemCollection;

    private ListenerRegistration userListsSnapshotListener;
    private ListenerRegistration itemsSnapshotListener;

    private final SingleLiveEvent<List<UserList>> eventDeleteUserList;

    private boolean recentLocalChanges;

    public UtilSnapshotListeners(CollectionReference userListCollection,
                                 CollectionReference itemCollection,
                                 IUserRepository userRepository,
                                 FirebaseFirestore firestore) {
        this.userListFlowable = initUserListFlowable();
        this.userListCollection = userListCollection;
        this.itemCollection = itemCollection;
        this.eventDeleteUserList = new SingleLiveEvent<>();
        recentLocalChanges = false;
        initFirebaseAuth(userRepository, firestore);
    }


    Flowable<List<UserList>> getUserListFlowable() {
        return userListFlowable;
    }

    Flowable<List<Item>> getItemFlowable(String userListId) {
        return initItemFlowable(userListId);
    }

    SingleLiveEvent<List<UserList>> getEventDeleteUserList() {
        return eventDeleteUserList;
    }


    private void initFirebaseAuth(IUserRepository userRepository, FirebaseFirestore firestore) {
        userRepository.userSignedOutObservable().observeForever(signedOut -> {
            if (!signedOut) {
                return;
            }

            if (userListsSnapshotListener != null) {
                userListsSnapshotListener.remove();
            }
            if (itemsSnapshotListener != null) {
                itemsSnapshotListener.remove();
            }

            // Clear persistence storage
            // https://firebase.google.com/support/release-notes/android#version_2010
            firestore.clearPersistence();
        });
    }


    private Flowable<List<UserList>> initUserListFlowable() {
        return Flowable.create(
                this::userListQuerySnapshot,
                BackpressureStrategy.BUFFER
        );
    }

    private void userListQuerySnapshot(FlowableEmitter<List<UserList>> emitter) {
        // this saves a reference to the listener
        this.userListsSnapshotListener = userListCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getUserListSnapshotListener(emitter));

        emitter.setCancellable(() -> {
            if (userListsSnapshotListener != null) {
                userListsSnapshotListener.remove();
            }
        });
    }

    private EventListener<QuerySnapshot> getUserListSnapshotListener(FlowableEmitter<List<UserList>> emitter) {
        return (queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots == null) {
                return;
            }

            if (checkForErrorFromQuery(queryDocumentSnapshots, e)) {
                UtilExceptions.throwException(e);
                emitter.onError(e);
            } else if (shouldReturn(queryDocumentSnapshots)) {
                return;
            }

            if (eventDeleteUserList.hasObservers()) {
                checkIfUserListDeleted(queryDocumentSnapshots);
            }

            emitter.onNext(queryDocumentSnapshots.toObjects(UserList.class));
        };
    }

    private void checkIfUserListDeleted(QuerySnapshot queryDocumentSnapshots) {
        List<UserList> deletedUserLists = new ArrayList<>();
        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
            if (change.getType() == DocumentChange.Type.REMOVED) {
                deletedUserLists.add(change.getDocument().toObject(UserList.class));
            }
        }
        if (deletedUserLists.isEmpty()) {
            return;
        }
        eventDeleteUserList.postValue(deletedUserLists);
    }


    private Flowable<List<Item>> initItemFlowable(String userListId) {
        return Flowable.create(
                emitter -> itemQuerySnapshot(emitter, userListId),
                BackpressureStrategy.BUFFER
        );
    }

    private void itemQuerySnapshot(FlowableEmitter<List<Item>> emitter, String userListId) {
        this.itemsSnapshotListener = itemCollection
                .whereEqualTo(FIELD_ITEM_LIST_ID, userListId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getItemSnapshotListener(emitter));

        emitter.setCancellable(() -> {
            if (itemsSnapshotListener != null) {
                itemsSnapshotListener.remove();
            }
        });
    }

    private EventListener<QuerySnapshot> getItemSnapshotListener(FlowableEmitter<List<Item>> emitter) {
        return (queryDocumentSnapshots, e) -> {
            if (queryDocumentSnapshots == null) {
                return;
            }

            if (checkForErrorFromQuery(queryDocumentSnapshots, e)) {
                UtilExceptions.throwException(e);
                emitter.onError(e);
            } else if (shouldReturn(queryDocumentSnapshots)) {
                return;
            }

            emitter.onNext(queryDocumentSnapshots.toObjects(Item.class));
        };
    }


    private boolean checkForErrorFromQuery(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            return true;
        } else if (queryDocumentSnapshots == null) {
            Crashlytics.log(Log.ERROR, RemoteRepositoryImpl.class.getSimpleName(), "QueryDocumentSnapshot is null");
            return true;
        }
        return false;
    }

    private boolean shouldReturn(QuerySnapshot queryDocumentSnapshots) {
        if (isRecentLocalChanges()) {
            recentLocalChanges = false;
            return true;
        } else if (fromLocalCache(queryDocumentSnapshots)) {
            recentLocalChanges = true;
            return false;
        }
        return false;
    }

    /**
     * Because there were recent local changes, I can assume that this payload is from the server
     * - which is identical of the query that just came from the local cache - thus it can be skipped.
     */
    private boolean isRecentLocalChanges() {
        return recentLocalChanges;
    }

    /**
     * This payload is from the local cache, post a local change.
     */
    private boolean fromLocalCache(QuerySnapshot queryDocumentSnapshots) {
        return queryDocumentSnapshots.getMetadata().hasPendingWrites();
    }
}
