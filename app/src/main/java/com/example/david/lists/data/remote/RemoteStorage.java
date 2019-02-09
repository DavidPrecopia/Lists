package com.example.david.lists.data.remote;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.LiveData;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;

import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ITEM_LIST_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_LISTS_COLLECTION;

public final class RemoteStorage implements IRemoteStorageContract {

    private final FirebaseFirestore firestore;
    private final CollectionReference userListsCollection;
    private final CollectionReference itemsCollection;

    private ListenerRegistration userListsSnapshotListener;
    private ListenerRegistration itemsSnapshotListener;

    private final SingleLiveEvent<List<UserList>> eventDeleteUserList;

    private boolean recentLocalChanges;

    public RemoteStorage(FirebaseFirestore firestore) {
        this.firestore = firestore;
        DocumentReference userDoc = firestore.collection(USER_COLLECTION).document(getUserId());
        userListsCollection = userDoc.collection(USER_LISTS_COLLECTION);
        itemsCollection = userDoc.collection(ITEMS_COLLECTION);
        eventDeleteUserList = new SingleLiveEvent<>();
        recentLocalChanges = false;

        init();
    }

    private void init() {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (UtilUser.signedOut()) {
                if (userListsSnapshotListener != null) {
                    userListsSnapshotListener.remove();
                }
                if (itemsSnapshotListener != null) {
                    itemsSnapshotListener.remove();
                }
            }
        });
    }


    @Override
    public Flowable<List<UserList>> getUserLists() {
        return Flowable.create(
                this::userListQuerySnapshot,
                BackpressureStrategy.BUFFER
        );
    }

    private void userListQuerySnapshot(FlowableEmitter<List<UserList>> emitter) {
        this.userListsSnapshotListener = userListsCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getGroupSnapshotListener(emitter));

        emitter.setCancellable(() -> {
            if (userListsSnapshotListener != null) {
                userListsSnapshotListener.remove();
            }
        });
    }

    private EventListener<QuerySnapshot> getGroupSnapshotListener(FlowableEmitter<List<UserList>> emitter) {
        return (queryDocumentSnapshots, e) -> {
            if (errorFromQuery(queryDocumentSnapshots, e)) {
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


    @Override
    public Flowable<List<Item>> getItems(String userListId) {
        return Flowable.create(
                emitter -> itemQuerySnapshot(emitter, userListId),
                BackpressureStrategy.BUFFER
        );
    }

    private void itemQuerySnapshot(FlowableEmitter<List<Item>> emitter, String userListId) {
        this.itemsSnapshotListener = itemsCollection
                .whereEqualTo(FIELD_ITEM_LIST_ID, userListId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getItemSnapshot(emitter));

        emitter.setCancellable(() -> {
            if (itemsSnapshotListener != null) {
                itemsSnapshotListener.remove();
            }
        });
    }

    private EventListener<QuerySnapshot> getItemSnapshot(FlowableEmitter<List<Item>> emitter) {
        return (queryDocumentSnapshots, e) -> {
            if (errorFromQuery(queryDocumentSnapshots, e)) {
                emitter.onError(e);
            } else if (shouldReturn(queryDocumentSnapshots)) {
                return;
            }

            emitter.onNext(queryDocumentSnapshots.toObjects(Item.class));
        };
    }


    private boolean errorFromQuery(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            return true;
        } else if (queryDocumentSnapshots == null) {
            Crashlytics.log(Log.ERROR, RemoteStorage.class.getSimpleName(), "QueryDocumentSnapshot is null");
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


    @Override
    public void addUserList(UserList userList) {
        DocumentReference documentRef = userListsCollection.document();
        UserList newUserList = new UserList(documentRef.getId(), userList);
        add(documentRef, newUserList);
    }

    @Override
    public void addItem(Item item) {
        DocumentReference documentRef = itemsCollection.document();
        Item newItem = new Item(documentRef.getId(), item);
        add(documentRef, newItem);
    }

    private void add(DocumentReference documentRef, Object object) {
        documentRef.set(object)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        WriteBatch writeBatch = firestore.batch();
        for (UserList userList : userLists) {
            writeBatch.delete(getUserListDocument(userList.getId()));
        }
        writeBatch.commit()
                .addOnSuccessListener(successfullyDeleteUserLists())
                .addOnFailureListener(this::onFailure);
    }

    private OnSuccessListener<Void> successfullyDeleteUserLists() {
        return aVoid -> userListsCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(this::reorderConsecutively)
                .addOnFailureListener(this::onFailure);
    }

    @Override
    public void deleteItems(List<Item> items) {
        WriteBatch batch = firestore.batch();
        for (Item item : items) {
            batch.delete(getItemDocument(item.getId()));
        }
        batch.commit()
                .addOnSuccessListener(successfullyDeleteItems(items.get(0).getUserListId()))
                .addOnFailureListener(this::onFailure);
    }

    private OnSuccessListener<Void> successfullyDeleteItems(String groupId) {
        return aVoid -> itemsCollection
                .whereEqualTo(FIELD_ITEM_LIST_ID, groupId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(this::reorderConsecutively)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void renameUserList(String userListId, String newName) {
        rename(getUserListDocument(userListId), newName);
    }

    @Override
    public void renameItem(String itemId, String newName) {
        rename(getItemDocument(itemId), newName);
    }

    private void rename(DocumentReference documentReference, String newName) {
        documentReference
                .update(FIELD_TITLE, newName)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void updateUserListPosition(UserList userList, int oldPosition, int newPosition) {
        getUserListDocument(userList.getId())
                .update(FIELD_POSITION, getNewTemporaryPosition(oldPosition, newPosition))
                .addOnSuccessListener(aVoid ->
                        userListsCollection
                                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener(this::reorderConsecutively)
                                .addOnFailureListener(this::onFailure)
                )
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void updateItemPosition(Item item, int oldPosition, int newPosition) {
        getItemDocument(item.getId())
                .update(FIELD_POSITION, getNewTemporaryPosition(oldPosition, newPosition))
                .addOnSuccessListener(aVoid ->
                        itemsCollection
                                .whereEqualTo(FIELD_ITEM_LIST_ID, item.getUserListId())
                                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener(this::reorderConsecutively)
                                .addOnFailureListener(this::onFailure)
                )
                .addOnFailureListener(this::onFailure);
    }

    private double getNewTemporaryPosition(int oldPosition, int newPosition) {
        return newPosition > oldPosition ? newPosition + 0.5 : newPosition - 0.5;
    }


    private void reorderConsecutively(QuerySnapshot queryDocumentSnapshots) {
        int newPosition = 0;
        WriteBatch batch = firestore.batch();
        for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
            if (snapshot.getDouble(FIELD_POSITION) != newPosition) {
                batch.update(snapshot.getReference(), FIELD_POSITION, newPosition);
            }
            newPosition++;
        }
        batch.commit();
    }


    private DocumentReference getUserListDocument(String groupId) {
        return userListsCollection.document(groupId);
    }

    private DocumentReference getItemDocument(String itemId) {
        return itemsCollection.document(itemId);
    }


    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public LiveData<List<UserList>> getEventUserListDeleted() {
        return eventDeleteUserList;
    }


    private void onFailure(Exception exception) {
        UtilExceptions.throwException(exception);
    }
}