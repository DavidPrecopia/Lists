package com.example.david.lists.ui.viewmodels;

import android.app.Application;

import com.example.david.lists.BuildConfig;
import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.EditingInfo;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.ui.adapaters.GroupAdapter;
import com.example.david.lists.ui.view.TouchHelperCallback;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilRecyclerView;
import com.example.david.lists.util.UtilUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

public final class GroupViewModel extends AndroidViewModel
        implements IGroupViewModelContract,
        TouchHelperCallback.TouchCallback,
        TouchHelperCallback.IStartDragListener,
        UtilRecyclerView.PopUpMenuCallback {

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final List<Group> groupList;
    private final GroupAdapter adapter;
    private final ItemTouchHelper touchHelper;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final SingleLiveEvent<Group> eventOpenGroup;
    private final SingleLiveEvent<String> eventDisplayError;
    private final SingleLiveEvent<String> eventNotifyUserOfDeletion;
    private final SingleLiveEvent<String> eventAdd;
    private final SingleLiveEvent<EditingInfo> eventEdit;

    private final SingleLiveEvent<Void> eventSignOut;
    private final SingleLiveEvent<Void> eventSignIn;

    private final List<Group> tempGroups;
    private int tempGroupPosition = -1;

    public GroupViewModel(@NonNull Application application, IModelContract model) {
        super(application);
        this.model = model;
        disposable = new CompositeDisposable();
        adapter = new GroupAdapter(this, this, this);
        touchHelper = new ItemTouchHelper(new TouchHelperCallback(this));
        groupList = new ArrayList<>();
        eventOpenGroup = new SingleLiveEvent<>();
        eventDisplayLoading = new MutableLiveData<>();
        eventDisplayError = new SingleLiveEvent<>();
        eventNotifyUserOfDeletion = new SingleLiveEvent<>();
        eventAdd = new SingleLiveEvent<>();
        eventEdit = new SingleLiveEvent<>();
        eventSignOut = new SingleLiveEvent<>();
        eventSignIn = new SingleLiveEvent<>();

        this.tempGroups = new ArrayList<>();

        init();
    }

    private void init() {
        eventDisplayLoading.setValue(true);
        getAllGroups();
    }


    private void getAllGroups() {
        disposable.add(model.getAllGroups()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(groupsSubscriber())
        );
    }

    private DisposableSubscriber<List<Group>> groupsSubscriber() {
        return new DisposableSubscriber<List<Group>>() {
            @Override
            public void onNext(List<Group> groups) {
                updateGroupList(groups);
                updateUi();
            }

            @Override
            public void onError(Throwable t) {
                if (BuildConfig.DEBUG) Timber.e(t);
                eventDisplayError.setValue(
                        getStringResource(R.string.error_msg_generic)
                );
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void updateGroupList(List<Group> groups) {
        this.groupList.clear();
        this.groupList.addAll(groups);
    }

    private void updateUi() {
        if (groupList.isEmpty()) {
            eventDisplayError.setValue(
                    getStringResource(R.string.error_msg_no_groups)
            );
        } else {
            eventDisplayLoading.setValue(false);
        }
        adapter.swapData(groupList);
    }


    @Override
    public void groupClicked(Group group) {
        eventOpenGroup.setValue(group);
    }

    @Override
    public void addButtonClicked() {
        eventAdd.setValue(getStringResource(R.string.hint_add_group));
    }

    @Override
    public void add(String title) {
        model.addGroup(new Group(title, this.groupList.size()));
    }


    @Override
    public void dragging(int fromPosition, int toPosition) {
        Collections.swap(groupList, fromPosition, toPosition);
        adapter.move(fromPosition, toPosition);
    }

    @Override
    public void movedPermanently(int newPosition) {
        Group group = groupList.get(newPosition);
        model.updateGroupPosition(
                group,
                group.getPosition(),
                newPosition
        );
    }


    @Override
    public void edit(int position) {
        eventEdit.setValue(new EditingInfo(groupList.get(position)));
    }

    @Override
    public void changeTitle(EditingInfo editingInfo, String newTitle) {
        model.renameGroup(editingInfo.getId(), newTitle);
    }


    @Override
    public void delete(int position) {
        adapter.remove(position);
        tempGroups.add(groupList.get(position));
        tempGroupPosition = position;

        eventNotifyUserOfDeletion.setValue(
                getStringResource(R.string.message_group_deletion)
        );
    }

    @Override
    public void swipedLeft(int position) {
        delete(position);
    }

    @Override
    public void undoRecentDeletion() {
        if (tempGroups.isEmpty() || tempGroupPosition < 0) {
            throw new UnsupportedOperationException(
                    getStringResource(R.string.error_invalid_action_undo_deletion)
            );
        }
        reAdd();
    }

    private void reAdd() {
        int lastDeletedPosition = tempGroups.size() - 1;
        adapter.reAdd(
                tempGroupPosition,
                tempGroups.get(lastDeletedPosition)
        );
        tempGroups.remove(lastDeletedPosition);
    }

    @Override
    public void deletionNotificationTimedOut() {
        if (tempGroups.isEmpty()) {
            return;
        }
        List<Group> groups = new ArrayList<>(tempGroups);
        model.deleteGroups(groups);
        tempGroups.clear();
    }


    @Override
    public void requestDrag(RecyclerView.ViewHolder viewHolder) {
        touchHelper.startDrag(viewHolder);
    }


    @Override
    public void signIn() {
        if (UtilUser.isAnonymous()) {
            eventSignIn.call();
        } else {
            throw new UnsupportedOperationException(
                    getStringResource(R.string.error_sign_in_when_not_anonymous)
            );
        }
    }

    @Override
    public void signOut() {
        eventSignOut.call();
    }


    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public ItemTouchHelper getItemTouchHelper() {
        return touchHelper;
    }

    @Override
    public LiveData<Group> getEventOpenGroup() {
        return eventOpenGroup;
    }

    @Override
    public LiveData<Boolean> getEventDisplayLoading() {
        return eventDisplayLoading;
    }

    @Override
    public LiveData<String> getEventDisplayError() {
        return eventDisplayError;
    }

    @Override
    public LiveData<String> getEventNotifyUserOfDeletion() {
        return eventNotifyUserOfDeletion;
    }

    @Override
    public LiveData<String> getEventAdd() {
        return eventAdd;
    }

    @Override
    public LiveData<EditingInfo> getEventEdit() {
        return eventEdit;
    }

    @Override
    public LiveData<Void> getEventSignOut() {
        return eventSignOut;
    }

    @Override
    public LiveData<Void> getEventSignIn() {
        return eventSignIn;
    }


    private String getStringResource(int resId) {
        return getApplication().getString(resId);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}