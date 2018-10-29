package com.example.david.lists.data.remote;

import android.app.Application;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.local.ILocalStorageContract;
import com.example.david.lists.data.local.LocalStorage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import timber.log.Timber;

import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ITEM_USER_LIST_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_LISTS_COLLECTION;
import static com.example.david.lists.util.UtilRxJava.completableIoAccess;

public final class RemoteStorage implements IRemoteStorageContract {

    private final FirebaseFirestore firestore;
    private final CollectionReference userListsCollection;
    private final CollectionReference itemsCollection;

    private final ILocalStorageContract localStorage;

    /**
     * When SnapshotListener first subscribes to a Collection,
     * Firestore response with all Documents in that Collection regardless
     * of whether or not they were modified.
     * This flag keeps track of whether or not this is that initial payload.
     * This device will be notified of any and all modifications that
     * occurred when this device was offline post the initial payload.
     */
    private static Boolean initialResponsePayload;


    private static RemoteStorage instance;

    public static IRemoteStorageContract getInstance(Application application) {
        if (instance == null) {
            instance = new RemoteStorage(application);
        }
        return instance;
    }

    private RemoteStorage(Application application) {
        firestore = FirebaseFirestore.getInstance();
        userListsCollection = firestore.collection(USER_LISTS_COLLECTION);
        itemsCollection = firestore.collection(ITEMS_COLLECTION);
        localStorage = LocalStorage.getInstance(application);

        initialResponsePayload = true;

        init();
    }


    private void init() {
        userListsCollection.addSnapshotListener(MetadataChanges.INCLUDE, userListsCollectionListener());
        itemsCollection.addSnapshotListener(MetadataChanges.INCLUDE, itemsCollectionListener());
    }

    private EventListener<QuerySnapshot> userListsCollectionListener() {
        return (queryDocumentSnapshots, e) -> {
            if (shouldReturn(queryDocumentSnapshots, e)) {
                return;
            }
            assert queryDocumentSnapshots != null;
            processUserListSnapshot(queryDocumentSnapshots);
        };
    }

    private void processUserListSnapshot(QuerySnapshot queryDocumentSnapshots) {
        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
            Timber.i("For-Loop");
            switch (change.getType()) {
                case ADDED:
                    Timber.i("ADDED");
                    completableIoAccess(Completable.fromAction(() ->
                            localStorage.addUserList(
                                    getUserListFromDocument(change.getDocument())
                            ))
                    );
                    break;
                case MODIFIED:
                    Timber.i("MOD");
                    completableIoAccess(Completable.fromAction(() ->
                            localStorage.updateUserList(
                                    getUserListFromDocument(change.getDocument())
                            )
                    ));
                    break;
                case REMOVED:
                    Timber.i("REMOVED");
                    completableIoAccess(Completable.fromAction(() ->
                            localStorage.deleteUserLists(
                                    Collections.singletonList(
                                            getUserListFromDocument(change.getDocument())
                                    )
                            )
                    ));
                    break;
            }
        }
    }

    private UserList getUserListFromDocument(DocumentSnapshot document) {
        return document.toObject(UserList.class);
    }

    private EventListener<QuerySnapshot> itemsCollectionListener() {
        return (queryDocumentSnapshots, e) -> {
            if (shouldReturn(queryDocumentSnapshots, e)) {
                return;
            }
            assert queryDocumentSnapshots != null;
            processItemSnapshot(queryDocumentSnapshots);
        };
    }

    private void processItemSnapshot(QuerySnapshot queryDocumentSnapshots) {
        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    completableIoAccess(Completable.fromAction(() ->
                            localStorage.addItem(
                                    getItemFromDocument(change.getDocument())
                            ))
                    );
                    break;
                case MODIFIED:
                    completableIoAccess(Completable.fromAction(() ->
                            localStorage.updateItem(
                                    getItemFromDocument(change.getDocument())
                            )
                    ));
                    break;
                case REMOVED:
                    completableIoAccess(Completable.fromAction(() ->
                            localStorage.deleteItems(
                                    Collections.singletonList(
                                            getItemFromDocument(change.getDocument())
                                    )
                            )
                    ));
                    break;
            }
        }
    }

    private Item getItemFromDocument(DocumentSnapshot document) {
        return document.toObject(Item.class);
    }

    private boolean shouldReturn(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            onFailure(e);
            return true;
        } else if (initialResponsePayload) {
            initialResponsePayload = false;
            return true;
        } else{
            // If this Snapshot listener is being invoked modification,
            // ignore it.
            return queryDocumentSnapshots.getMetadata().hasPendingWrites();
        }
    }


    @Override
    public String addUserList(UserList userList) {
        DocumentReference documentRef = userListsCollection.document();
        String id = documentRef.getId();
        add(documentRef, new UserList(id, userList));
        return id;
    }

    @Override
    public String addItem(Item item) {
        final DocumentReference documentRef = itemsCollection.document();
        final String id = documentRef.getId();
        add(documentRef, new Item(id, item));
        return id;
    }

    private void add(DocumentReference documentRef, Object object) {
        documentRef.set(object)
                .addOnFailureListener(this::onFailure);
    }


    /**
     * Batch deletion of {@link UserList} and {@link Item} are separate
     * so I can easily refactor to Cloud Functions down the road.
     */
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
                    .whereEqualTo(FIELD_ID, userListId)
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
        updateUserListPositions(
                userListsCollection,
                decrementPositions(getUserListDocument(userList.getId()), newPosition),
                oldPosition,
                newPosition
        );
    }

    @Override
    public void updateUserListPositionsIncrement(UserList userList, int oldPosition, int newPosition) {
        updateUserListPositions(
                userListsCollection,
                incrementPositions(getUserListDocument(userList.getId()), newPosition),
                oldPosition,
                newPosition
        );
    }

    private void updateUserListPositions(CollectionReference collectionReference, OnSuccessListener<QuerySnapshot> successListener, int oldPosition, int newPosition) {
        int lowerPosition = getLowerPosition(oldPosition, newPosition);
        int higherPosition = getHigherPosition(oldPosition, newPosition);
        collectionReference
                .whereGreaterThanOrEqualTo(FIELD_POSITION, lowerPosition)
                .whereLessThanOrEqualTo(FIELD_POSITION, higherPosition)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(this::onFailure);
    }

    @Override
    public void updateItemPositionsDecrement(Item item, int oldPosition, int newPosition) {
        updateItemPositions(
                item.getUserListId(),
                itemsCollection,
                decrementPositions(getItemDocument(item.getId()), newPosition),
                oldPosition,
                newPosition
        );
    }

    @Override
    public void updateItemPositionsIncrement(Item item, int oldPosition, int newPosition) {
        updateItemPositions(
                item.getUserListId(),
                itemsCollection,
                incrementPositions(getItemDocument(item.getId()), newPosition),
                oldPosition,
                newPosition
        );
    }

    private void updateItemPositions(String userListId, CollectionReference collectionReference, OnSuccessListener<QuerySnapshot> successListener, int oldPosition, int newPosition) {
        int lowerPosition = getLowerPosition(oldPosition, newPosition);
        int higherPosition = getHigherPosition(oldPosition, newPosition);
        collectionReference
                .whereEqualTo(FIELD_ITEM_USER_LIST_ID, userListId)
                .whereGreaterThanOrEqualTo(FIELD_POSITION, lowerPosition)
                .whereLessThanOrEqualTo(FIELD_POSITION, higherPosition)
                .get()
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


    private void onFailure(Exception exception) {
        Timber.e(exception);
    }
}
