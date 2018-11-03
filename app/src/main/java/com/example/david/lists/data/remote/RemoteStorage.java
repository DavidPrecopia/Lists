package com.example.david.lists.data.remote;

import android.app.Application;

import com.example.david.lists.data.datamodel.Item;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.local.ILocalStorageContract;
import com.example.david.lists.data.local.LocalStorage;
import com.example.david.lists.util.UtilUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import timber.log.Timber;

import static com.example.david.lists.util.UtilRxJava.completableIoAccess;

public final class RemoteStorage implements IRemoteStorageContract {

    private final RemoteDao dao;
    private final ILocalStorageContract localStorage;

    /**
     * When SnapshotListener first subscribes to a Collection,
     * Firestore response with all Documents in that Collection regardless
     * of whether or not they were modified.
     * This flag keeps track of whether or not this is that initial payload.
     * This device will be notified of any and all modifications that
     * occurred when this device was offline post the initial payload.
     */
    private static boolean initialResponsePayload;


    private static RemoteStorage instance;

    public static IRemoteStorageContract getInstance(Application application) {
        if (instance == null) {
            instance = new RemoteStorage(application);
        }
        return instance;
    }

    private RemoteStorage(Application application) {
        dao = RemoteDao.getInstance(userListsListener(), itemsListener());
        localStorage = LocalStorage.getInstance(application);
        initialResponsePayload = true;
    }

    private EventListener<QuerySnapshot> userListsListener() {
        return (queryDocumentSnapshots, e) -> {
            if (shouldReturn(queryDocumentSnapshots, e)) {
                return;
            }
            assert queryDocumentSnapshots != null;
            processUserListSnapshot(queryDocumentSnapshots);
        };
    }

    private EventListener<QuerySnapshot> itemsListener() {
        return (queryDocumentSnapshots, e) -> {
            if (shouldReturn(queryDocumentSnapshots, e)) {
                return;
            }
            assert queryDocumentSnapshots != null;
            processItemSnapshot(queryDocumentSnapshots);
        };
    }


    private void processUserListSnapshot(QuerySnapshot queryDocumentSnapshots) {
        List<UserList> added = new ArrayList<>();
        List<UserList> modified = new ArrayList<>();
        List<UserList> removed = new ArrayList<>();
        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    added.add(getUserListFromDocument(change.getDocument()));
                    break;
                case MODIFIED:
                    modified.add(getUserListFromDocument(change.getDocument()));
                    break;
                case REMOVED:
                    removed.add(getUserListFromDocument(change.getDocument()));
                    break;
            }
        }
        localAddUserLists(added);
        localModifyUserList(modified);
        localDeleteUserList(removed);
    }

    private UserList getUserListFromDocument(DocumentSnapshot document) {
        return document.toObject(UserList.class);
    }

    private void localAddUserLists(List<UserList> added) {
        if (added.isEmpty()) {
            return;
        }
        completableIoAccess(Completable.fromAction(() ->
                localStorage.addUserList(added))
        );
    }

    private void localModifyUserList(List<UserList> modified) {
        if (modified.isEmpty()) {
            return;
        }
        completableIoAccess(Completable.fromAction(() ->
                localStorage.updateUserList(modified))
        );
    }

    private void localDeleteUserList(List<UserList> removed) {
        if (removed.isEmpty()) {
            return;
        }
        completableIoAccess(Completable.fromAction(() ->
                localStorage.deleteUserLists(removed))
        );
    }


    private void processItemSnapshot(QuerySnapshot queryDocumentSnapshots) {
        List<Item> added = new ArrayList<>();
        List<Item> modified = new ArrayList<>();
        List<Item> removed = new ArrayList<>();
        for (DocumentChange change : queryDocumentSnapshots.getDocumentChanges()) {
            switch (change.getType()) {
                case ADDED:
                    added.add(getItemFromDocument(change.getDocument()));
                    break;
                case MODIFIED:
                    modified.add(getItemFromDocument(change.getDocument()));
                    break;
                case REMOVED:
                    removed.add(getItemFromDocument(change.getDocument()));
                    break;
            }
        }
        localAddItems(added);
        localModifyItem(modified);
        localDeleteItem(removed);
    }

    private Item getItemFromDocument(DocumentSnapshot document) {
        return document.toObject(Item.class);
    }

    private void localAddItems(List<Item> added) {
        if (added == null) {
            return;
        }
        completableIoAccess(Completable.fromAction(() ->
                localStorage.addItems(added)
        ));
    }

    private void localModifyItem(List<Item> modified) {
        if (modified == null) {
            return;
        }
        completableIoAccess(Completable.fromAction(() ->
                localStorage.updateItem(modified)
        ));
    }

    private void localDeleteItem(List<Item> removed) {
        if (removed == null) {
            return;
        }
        completableIoAccess(Completable.fromAction(() ->
                localStorage.deleteItems(removed)
        ));
    }

    private boolean shouldReturn(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
        if (e != null) {
            onFailure(e);
            return true;
        } if (UtilUser.recentlySignedIn()) {
            return false;
        } else if (initialResponsePayload) {
            initialResponsePayload = false;
            return true;
        } else {
            // If this Snapshot listener is being invoked because
            // of a local modification, ignore it.
            return queryDocumentSnapshots.getMetadata().hasPendingWrites();
        }
    }


    @Override
    public UserList addUserList(UserList userList) {
        return dao.addUserList(userList);
    }

    @Override
    public Item addItem(Item item) {
        return dao.addItem(item);
    }


    @Override
    public void deleteUserLists(List<UserList> userLists) {
        dao.deleteUserLists(userLists);
    }

    @Override
    public void deleteItems(List<Item> items) {
        dao.deleteItems(items);
    }


    @Override
    public void renameUserList(String userListId, String newName) {
        dao.renameUserList(userListId, newName);
    }

    @Override
    public void renameItem(String itemId, String newName) {
        dao.renameItem(itemId, newName);
    }


    @Override
    public void updateUserListPositionsDecrement(UserList userList, int oldPosition, int newPosition) {
        dao.updateUserListPositionsDecrement(userList, oldPosition, newPosition);
    }

    @Override
    public void updateUserListPositionsIncrement(UserList userList, int oldPosition, int newPosition) {
        dao.updateUserListPositionsIncrement(userList, oldPosition, newPosition);
    }

    @Override
    public void updateItemPositionsDecrement(Item item, int oldPosition, int newPosition) {
        dao.updateItemPositionsDecrement(item, oldPosition, newPosition);
    }


    @Override
    public void updateItemPositionsIncrement(Item item, int oldPosition, int newPosition) {
        dao.updateItemPositionsIncrement(item, oldPosition, newPosition);
    }


    private void onFailure(Exception exception) {
        Timber.e(exception);
    }
}
