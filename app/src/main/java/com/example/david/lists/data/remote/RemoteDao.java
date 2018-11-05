package com.example.david.lists.data.remote;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ITEM_USER_LIST_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_USER_ID;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_LISTS_COLLECTION;
import static com.example.david.lists.util.UtilUser.getUserId;

final class RemoteDao {

    private final FirebaseFirestore firestore;
    private final CollectionReference userListsCollection;
    private final CollectionReference itemsCollection;


    private static volatile RemoteDao remoteDao;

    static RemoteDao getInstance(EventListener<QuerySnapshot> userListsListener, EventListener<QuerySnapshot> itemsListener) {
        if (remoteDao == null) {
            remoteDao = new RemoteDao(userListsListener, itemsListener);
        }
        return remoteDao;
    }

    private RemoteDao(EventListener<QuerySnapshot> userListsListener, EventListener<QuerySnapshot> itemsListener) {
        firestore = FirebaseFirestore.getInstance();
        // Receiving the same message as: https://github.com/invertase/react-native-firebase/issues/1131
        firestore.setFirestoreSettings(
                new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .setTimestampsInSnapshotsEnabled(true)
                        .build()
        );


        userListsCollection = firestore.collection(USER_LISTS_COLLECTION);
        itemsCollection = firestore.collection(ITEMS_COLLECTION);
        init(userListsListener, itemsListener);
    }

    private void init(EventListener<QuerySnapshot> userListsListener, EventListener<QuerySnapshot> itemsListener) {
        userListsCollection
                .whereEqualTo(FIELD_USER_ID, getUserId())
                .addSnapshotListener(MetadataChanges.INCLUDE, userListsListener);
        itemsCollection
                .whereEqualTo(FIELD_USER_ID, getUserId())
                .addSnapshotListener(MetadataChanges.INCLUDE, itemsListener);
    }


    UserList addUserList(UserList userList) {
        DocumentReference documentRef = userListsCollection.document();
        UserList newUserList = new UserList(documentRef.getId(), getUserId(), userList);
        add(documentRef, newUserList);
        return newUserList;
    }

    Item addItem(Item item) {
        DocumentReference documentRef = itemsCollection.document();
        Item newItem = new Item(documentRef.getId(), getUserId(), item);
        add(documentRef, newItem);
        return newItem;
    }

    private void add(DocumentReference documentRef, Object object) {
        documentRef.set(object)
                .addOnFailureListener(this::onFailure);
    }


    /**
     * Batch deletion of {@link UserList} and {@link Item} are separate
     * so I can easily refactor to Cloud Functions down the road.
     */
    void deleteUserLists(List<UserList> userLists) {
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


    void deleteItems(List<Item> items) {
        WriteBatch batch = firestore.batch();
        for (Item item : items) {
            batch.delete(getItemDocument(item.getId()));
        }
        batch.commit().addOnFailureListener(this::onFailure);
    }


    void renameUserList(String userListId, String newName) {
        getUserListDocument(userListId)
                .update(FIELD_TITLE, newName)
                .addOnFailureListener(this::onFailure);
    }

    void renameItem(String itemId, String newName) {
        getItemDocument(itemId)
                .update(FIELD_TITLE, newName)
                .addOnFailureListener(this::onFailure);
    }


    void updateUserListPositionsDecrement(UserList userList, int oldPosition, int newPosition) {
        updatePositions(
                getUserListUpdatePositionsQuery(oldPosition, newPosition),
                decrementPositions(getUserListDocument(userList.getId()), newPosition)
        );
    }

    void updateUserListPositionsIncrement(UserList userList, int oldPosition, int newPosition) {
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


    void updateItemPositionsDecrement(Item item, int oldPosition, int newPosition) {
        updatePositions(
                getItemsUpdatePositionsQuery(item.getUserListId(), oldPosition, newPosition),
                decrementPositions(getItemDocument(item.getId()), newPosition)
        );
    }

    void updateItemPositionsIncrement(Item item, int oldPosition, int newPosition) {
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


    private void onFailure(Exception exception) {
        Timber.e(exception);
    }
}
