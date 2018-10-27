package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_LISTS_COLLECTION;

public final class RemoteStorage implements IRemoteStorageContract {

    private final FirebaseFirestore firestore;
    private final CollectionReference userListsCollection;
    private final CollectionReference itemsCollection;

    private static RemoteStorage instance;

    public static IRemoteStorageContract getInstance() {
        if (instance == null) {
            instance = new RemoteStorage();
        }
        return instance;
    }

    private RemoteStorage() {
        firestore = FirebaseFirestore.getInstance();
        userListsCollection = firestore.collection(USER_LISTS_COLLECTION);
        itemsCollection = firestore.collection(ITEMS_COLLECTION);
    }


    @Override
    public String addUserList(UserList userList) {
        return add(userListsCollection, userList);
    }

    @Override
    public String addItem(Item item) {
        return add(itemsCollection, item);
    }

    private String add(CollectionReference collectionReference, Object object) {
        DocumentReference documentRef = collectionReference.document();
        String id = documentRef.getId();
        documentRef.set(object).addOnFailureListener(this::onFailure);
        return id;
    }


    /**
     * Batch deletion of {@link UserList} and {@link Item} are separate
     * so I can easily refactor to Cloud Functions down the road.
     */
    @Override
    public void deleteUserLists(List<UserList> userLists) {
        List<Integer> userListIds = batchDeleteUserLists(userLists);
        prepareToBatchDeleteItems(userListIds);
    }

    private List<Integer> batchDeleteUserLists(List<UserList> userLists) {
        List<Integer> userListIds = new ArrayList<>();

        WriteBatch writeBatch = firestore.batch();
        for (UserList userList : userLists) {
            int id = userList.getId();
            userListIds.add(id);
            writeBatch.delete(getUserListDocument(id));
        }
        writeBatch.commit().addOnFailureListener(this::onFailure);

        return userListIds;
    }

    private void prepareToBatchDeleteItems(List<Integer> userListIds) {
        for (Integer userListId : userListIds) {
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
    public void renameUserList(int userListId, String newName) {
        getUserListDocument(userListId)
                .update(FIELD_TITLE, newName)
                .addOnFailureListener(this::onFailure);
    }

    @Override
    public void renameItem(int itemId, String newName) {
        getItemDocument(itemId)
                .update(FIELD_TITLE, newName)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void updateUserListPositionsDecrement(int userListId, int oldPosition, int newPosition) {
        updatePositions(
                userListsCollection,
                decrementPositions(getUserListDocument(userListId), newPosition),
                oldPosition,
                newPosition
        );
    }

    @Override
    public void updateUserListPositionsIncrement(int userListId, int oldPosition, int newPosition) {
        updatePositions(
                userListsCollection,
                incrementPositions(getUserListDocument(userListId), newPosition),
                oldPosition,
                newPosition
        );
    }

    @Override
    public void updateItemPositionsDecrement(int itemId, int oldPosition, int newPosition) {
        updatePositions(
                itemsCollection,
                decrementPositions(getItemDocument(itemId), newPosition),
                oldPosition,
                newPosition
        );
    }

    @Override
    public void updateItemPositionsIncrement(int itemId, int oldPosition, int newPosition) {
        updatePositions(
                itemsCollection,
                incrementPositions(getItemDocument(itemId), newPosition),
                oldPosition,
                newPosition
        );
    }

    private void updatePositions(CollectionReference collectionReference, OnSuccessListener<QuerySnapshot> successListener, int oldPosition, int newPosition) {
        int lowerPosition = oldPosition < newPosition ? oldPosition : newPosition;
        int higherPosition = oldPosition > newPosition ? oldPosition : newPosition;
        collectionReference
                .whereGreaterThanOrEqualTo(FIELD_POSITION, lowerPosition)
                .whereLessThanOrEqualTo(FIELD_POSITION, higherPosition)
                .get()
                .addOnSuccessListener(successListener)
                .addOnFailureListener(this::onFailure);
    }

    private OnSuccessListener<QuerySnapshot> decrementPositions(DocumentReference movedDocument, int newPosition) {
        return queryDocumentSnapshots -> {
            Timber.d("decrement");
            WriteBatch batch = firestore.batch();
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                Timber.d("decrement -- %s", queryDocumentSnapshots.size());
                int updatedPosition = Objects.requireNonNull(snapshot.getLong(FIELD_POSITION)).intValue() - 1;
                batch.update(snapshot.getReference(), FIELD_POSITION, updatedPosition);
            }
            batch.update(movedDocument, FIELD_POSITION, newPosition);
            batch.commit().addOnFailureListener(this::onFailure);
        };
    }

    private OnSuccessListener<QuerySnapshot> incrementPositions(DocumentReference movedDocument, int newPosition) {
        return queryDocumentSnapshots -> {
            Timber.d("increment");
            WriteBatch batch = firestore.batch();
            for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                Timber.d("increment -- %s", queryDocumentSnapshots.size());
                int updatedPosition = Objects.requireNonNull(snapshot.getLong(FIELD_POSITION)).intValue() + 1;
                batch.update(snapshot.getReference(), FIELD_POSITION, updatedPosition);
            }
            batch.update(movedDocument, FIELD_POSITION, newPosition);
            batch.commit().addOnFailureListener(this::onFailure);
        };
    }


    private DocumentReference getUserListDocument(int userListId) {
        return userListsCollection.document(intToString(userListId));
    }

    private DocumentReference getItemDocument(int itemId) {
        return itemsCollection.document(intToString(itemId));
    }


    private String intToString(int id) {
        return String.valueOf(id);
    }

    private void onFailure(Exception exception) {
        Timber.e(exception);
    }
}
