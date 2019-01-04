package com.example.david.lists.data.remote;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.datamodel.Item;
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
import com.google.firebase.firestore.FirebaseFirestoreSettings;
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

import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_ITEM_GROUP_ID;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_POSITION;
import static com.example.david.lists.data.datamodel.DataModelFieldConstants.FIELD_TITLE;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.GROUPS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.ITEMS_COLLECTION;
import static com.example.david.lists.data.remote.RemoteDatabaseConstants.USER_COLLECTION;

public final class RemoteStorage implements IRemoteStorageContract {

    private final FirebaseFirestore firestore;
    private final CollectionReference groupsCollection;
    private final CollectionReference itemsCollection;

    private ListenerRegistration groupsSnapshotListener;
    private ListenerRegistration itemsSnapshotListener;

    private final SingleLiveEvent<List<Group>> eventDeleteGroups;

    private boolean recentLocalChanges;


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
        DocumentReference userDoc = firestore.collection(USER_COLLECTION).document(getUserId());
        groupsCollection = userDoc.collection(GROUPS_COLLECTION);
        itemsCollection = userDoc.collection(ITEMS_COLLECTION);
        eventDeleteGroups = new SingleLiveEvent<>();
        recentLocalChanges = false;

        init();
    }

    private void init() {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (UtilUser.signedOut()) {
                if (groupsSnapshotListener != null) {
                    groupsSnapshotListener.remove();
                }
                if (itemsSnapshotListener != null) {
                    itemsSnapshotListener.remove();
                }
            }
        });
    }


    @Override
    public Flowable<List<Group>> getGroups() {
        return Flowable.create(
                this::groupQuerySnapshot,
                BackpressureStrategy.BUFFER
        );
    }

    private void groupQuerySnapshot(FlowableEmitter<List<Group>> emitter) {
        this.groupsSnapshotListener = groupsCollection
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .addSnapshotListener(MetadataChanges.INCLUDE, getGroupSnapshotListener(emitter));

        emitter.setCancellable(() -> {
            if (groupsSnapshotListener != null) {
                groupsSnapshotListener.remove();
            }
        });
    }

    private EventListener<QuerySnapshot> getGroupSnapshotListener(FlowableEmitter<List<Group>> emitter) {
        return (queryDocumentSnapshots, e) -> {
            if (errorFromQuery(queryDocumentSnapshots, e)) {
                emitter.onError(e);
            } else if (shouldReturn(queryDocumentSnapshots)) {
                return;
            }

            if (eventDeleteGroups.hasObservers()) {
                checkIfGroupDeleted(queryDocumentSnapshots);
            }

            emitter.onNext(queryDocumentSnapshots.toObjects(Group.class));
        };
    }

    private void checkIfGroupDeleted(QuerySnapshot queryDocumentSnapshots) {
        List<Group> deletedGroups = new ArrayList<>();
        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
            if (change.getType() == DocumentChange.Type.REMOVED) {
                deletedGroups.add(change.getDocument().toObject(Group.class));
            }
        }
        if (!deletedGroups.isEmpty()) {
            eventDeleteGroups.postValue(deletedGroups);
        }
    }


    @Override
    public Flowable<List<Item>> getItems(String groupId) {
        return Flowable.create(
                emitter -> itemQuerySnapshot(emitter, groupId),
                BackpressureStrategy.BUFFER
        );
    }

    private void itemQuerySnapshot(FlowableEmitter<List<Item>> emitter, String groupId) {
        this.itemsSnapshotListener = itemsCollection
                .whereEqualTo(FIELD_ITEM_GROUP_ID, groupId)
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
    public void addGroup(Group group) {
        DocumentReference documentRef = groupsCollection.document();
        Group newGroup = new Group(documentRef.getId(), group);
        add(documentRef, newGroup);
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
    public void deleteGroups(List<Group> groups) {
        WriteBatch writeBatch = firestore.batch();
        for (Group group : groups) {
            writeBatch.delete(getGroupDocument(group.getId()));
        }
        writeBatch.commit()
                .addOnSuccessListener(successfullyDeleteGroups())
                .addOnFailureListener(this::onFailure);
    }

    private OnSuccessListener<Void> successfullyDeleteGroups() {
        return aVoid -> groupsCollection
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
                .addOnSuccessListener(successfullyDeleteItems(items.get(0).getGroupId()))
                .addOnFailureListener(this::onFailure);
    }

    private OnSuccessListener<Void> successfullyDeleteItems(String groupId) {
        return aVoid -> itemsCollection
                .whereEqualTo(FIELD_ITEM_GROUP_ID, groupId)
                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(this::reorderConsecutively)
                .addOnFailureListener(this::onFailure);
    }


    @Override
    public void renameGroup(String groupId, String newName) {
        rename(getGroupDocument(groupId), newName);
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
    public void updateGroupPosition(Group group, int oldPosition, int newPosition) {
        getGroupDocument(group.getId())
                .update(FIELD_POSITION, evaluateNewPositions(oldPosition, newPosition))
                .addOnSuccessListener(aVoid ->
                        groupsCollection
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
                .update(FIELD_POSITION, evaluateNewPositions(oldPosition, newPosition))
                .addOnSuccessListener(aVoid ->
                        itemsCollection
                                .whereEqualTo(FIELD_ITEM_GROUP_ID, item.getGroupId())
                                .orderBy(FIELD_POSITION, Query.Direction.ASCENDING)
                                .get()
                                .addOnSuccessListener(this::reorderConsecutively)
                                .addOnFailureListener(this::onFailure)
                )
                .addOnFailureListener(this::onFailure);
    }

    private double evaluateNewPositions(int oldPosition, int newPosition) {
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


    private DocumentReference getGroupDocument(String groupId) {
        return groupsCollection.document(groupId);
    }

    private DocumentReference getItemDocument(String itemId) {
        return itemsCollection.document(itemId);
    }


    private String getUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }


    @Override
    public LiveData<List<Group>> getEventGroupDeleted() {
        return eventDeleteGroups;
    }


    private void onFailure(Exception exception) {
        UtilExceptions.throwException(exception);
    }
}