package com.example.david.lists.data.remote;

import com.example.david.lists.BuildConfig;
import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilUser;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.lifecycle.LiveData;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import timber.log.Timber;

import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ITEM_USER_LIST_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_USER_ID;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_LISTS_COLLECTION;

public final class RemoteStorage implements IRemoteStorageContract {

    private final FirebaseFirestore firestore;
    private final CollectionReference userListsCollection;
    private final CollectionReference itemsCollection;

    private ListenerRegistration userListsSnapshotListener;
    private ListenerRegistration itemsSnapshotListener;

    private final SingleLiveEvent<List<UserList>> eventDeleteUserLists;


    private static RemoteStorage instance;

    public static IRemoteStorageContract getInstance() {
        if (instance == null) {
            instance = new RemoteStorage();
        }
        return instance;
    }

    private RemoteStorage() {
        firestore = FirebaseFirestore.getInstance();
        firestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        );
        userListsCollection = firestore.collection(USER_LISTS_COLLECTION);
        itemsCollection = firestore.collection(ITEMS_COLLECTION);
        eventDeleteUserLists = new SingleLiveEvent<>();

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
                .whereEqualTo(FIELD_USER_ID, getUserId())
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getUserListSnapshot(emitter));
    }

    private EventListener<QuerySnapshot> getUserListSnapshot(FlowableEmitter<List<UserList>> emitter) {
        return (queryDocumentSnapshots, e) -> {
            if (e != null) {
                emitter.onError(e);
                return;
            } else if (queryDocumentSnapshots == null) {
                if (BuildConfig.DEBUG) Timber.e("QueryDocumentSnapshot is null");
                return;
            }
            emitter.onNext(queryDocumentSnapshots.toObjects(UserList.class));
        };
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
                .whereEqualTo(FIELD_ITEM_USER_LIST_ID, userListId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getItemSnapshot(emitter));
    }

    private EventListener<QuerySnapshot> getItemSnapshot(FlowableEmitter<List<Item>> emitter) {
        return (queryDocumentSnapshots, e) -> {
            if (e != null) {
                emitter.onError(e);
                return;
            } else if (queryDocumentSnapshots == null) {
                if (BuildConfig.DEBUG) Timber.e("QueryDocumentSnapshot is null");
                return;
            }
            emitter.onNext(queryDocumentSnapshots.toObjects(Item.class));
        };
    }


    @Override
    public void addUserList(UserList userList) {
        DocumentReference documentRef = userListsCollection.document();
        UserList newUserList = new UserList(documentRef.getId(), getUserId(), userList);
        add(documentRef, newUserList);
    }

    @Override
    public void addItem(Item item) {
        DocumentReference documentRef = itemsCollection.document();
        Item newItem = new Item(documentRef.getId(), getUserId(), item);
        add(documentRef, newItem);
    }

    private void add(DocumentReference documentRef, Object object) {
        documentRef.set(object)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        List<String> userListIds = batchDeleteUserLists(userLists);
        prepareToBatchDeleteItems(userListIds);
    }

    private List<String> batchDeleteUserLists(List<UserList> userLists) {
        List<String> userListIds = new ArrayList<>();

        WriteBatch writeBatch = firestore.batch();
        for (UserList userList : userLists) {
            String id = userList.getId();
            userListIds.add(id);
            writeBatch.delete(getUserListDocument(id));
        }
        writeBatch.commit().addOnFailureListener(this::onFailure);

        return userListIds;
    }

    private void prepareToBatchDeleteItems(List<String> userListIds) {
        for (String userListId : userListIds) {
            itemsCollection
                    .whereEqualTo(FIELD_USER_ID, getUserId())
                    .whereEqualTo(FIELD_ITEM_USER_LIST_ID, userListId)
                    .get()
                    .addOnSuccessListener(this::batchDeleteItems)
                    .addOnFailureListener(this::onFailure);
        }
    }

    private void batchDeleteItems(QuerySnapshot queryDocumentSnapshots) {
        WriteBatch batch = firestore.batch();
        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
            batch.delete(snapshot.getReference());
        }
        batch.commit().addOnFailureListener(this::onFailure);
    }

    @Override
    public void deleteItems(List<Item> items) {
        WriteBatch batch = firestore.batch();
        for (Item item : items) {
            batch.delete(getItemDocument(item.getId()));
        }
        batch.commit().addOnFailureListener(this::onFailure);
    }


    @Override
    public void renameUserList(String userListId, String newName) {
        getUserListDocument(userListId)
                .update(FIELD_TITLE, newName)
                .addOnFailureListener(this::onFailure);
    }

    @Override
    public void renameItem(String itemId, String newName) {
        getItemDocument(itemId)
                .update(FIELD_TITLE, newName)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void updateUserListPositionsDecrement(UserList userList, int oldPosition, int newPosition) {
        updatePositions(
                getUserListUpdatePositionsQuery(oldPosition, newPosition),
                decrementPositions(getUserListDocument(userList.getId()), newPosition)
        );
    }

    @Override
    public void updateUserListPositionsIncrement(UserList userList, int oldPosition, int newPosition) {
        updatePositions(
                getUserListUpdatePositionsQuery(oldPosition, newPosition),
                incrementPositions(getUserListDocument(userList.getId()), newPosition)
        );
    }

    private Query getUserListUpdatePositionsQuery(int oldPosition, int newPosition) {
        int lowerPosition = getLowerPosition(oldPosition, newPosition);
        int higherPosition = getHigherPosition(oldPosition, newPosition);
        return userListsCollection
                .whereEqualTo(FIELD_USER_ID, getUserId())
                .whereGreaterThanOrEqualTo(FIELD_POSITION, lowerPosition)
                .whereLessThanOrEqualTo(FIELD_POSITION, higherPosition);
    }


    @Override
    public void updateItemPositionsDecrement(Item item, int oldPosition, int newPosition) {
        updatePositions(
                getItemsUpdatePositionsQuery(item.getUserListId(), oldPosition, newPosition),
                decrementPositions(getItemDocument(item.getId()), newPosition)
        );
    }

    @Override
    public void updateItemPositionsIncrement(Item item, int oldPosition, int newPosition) {
        updatePositions(
                getItemsUpdatePositionsQuery(item.getUserListId(), oldPosition, newPosition),
                incrementPositions(getItemDocument(item.getId()), newPosition)
        );
    }

    private Query getItemsUpdatePositionsQuery(String userListId, int oldPosition, int newPosition) {
        int lowerPosition = getLowerPosition(oldPosition, newPosition);
        int higherPosition = getHigherPosition(oldPosition, newPosition);
        return itemsCollection
                .whereEqualTo(FIELD_USER_ID, getUserId())
                .whereEqualTo(FIELD_ITEM_USER_LIST_ID, userListId)
                .whereGreaterThanOrEqualTo(FIELD_POSITION, lowerPosition)
                .whereLessThanOrEqualTo(FIELD_POSITION, higherPosition);
    }


    private void updatePositions(Query query, OnSuccessListener<QuerySnapshot> successListener) {
        query.get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(this::onFailure);
    }

    private OnSuccessListener<QuerySnapshot> decrementPositions(DocumentReference movedDocument, int newPosition) {
        return queryDocumentSnapshots -> {
            WriteBatch batch = firestore.batch();
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                int updatedPosition = Objects.requireNonNull(snapshot.getLong(FIELD_POSITION)).intValue() - 1;
                batch.update(snapshot.getReference(), FIELD_POSITION, updatedPosition);
            }
            batch.update(movedDocument, FIELD_POSITION, newPosition);
            batch.commit().addOnFailureListener(this::onFailure);
        };
    }

    private OnSuccessListener<QuerySnapshot> incrementPositions(DocumentReference movedDocument, int newPosition) {
        return queryDocumentSnapshots -> {
            WriteBatch batch = firestore.batch();
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                int updatedPosition = Objects.requireNonNull(snapshot.getLong(FIELD_POSITION)).intValue() + 1;
                batch.update(snapshot.getReference(), FIELD_POSITION, updatedPosition);
            }
            batch.update(movedDocument, FIELD_POSITION, newPosition);
            batch.commit().addOnFailureListener(this::onFailure);
        };
    }

    private int getLowerPosition(int oldPosition, int newPosition) {
        return oldPosition < newPosition ? oldPosition : newPosition;
    }

    private int getHigherPosition(int oldPosition, int newPosition) {
        return oldPosition > newPosition ? oldPosition : newPosition;
    }


    private DocumentReference getUserListDocument(String userListId) {
        return userListsCollection.document(userListId);
    }

    private DocumentReference getItemDocument(String itemId) {
        return itemsCollection.document(itemId);
    }


    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public LiveData<List<UserList>> getEventUserListDeleted() {
        return eventDeleteUserLists;
    }


    private void onFailure(Exception exception) {
        if (BuildConfig.DEBUG) Timber.e(exception);
    }
}
