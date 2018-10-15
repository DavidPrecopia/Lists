package com.example.david.lists.widget;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.model.IModelContract;
import com.example.david.lists.ui.adapaters.UserListsAdapter;
import com.example.david.lists.ui.dialogs.EditingInfo;
import com.example.david.lists.ui.viewmodels.IListViewModelContract;

import java.util.ArrayList;
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

/**
 * Needs to implement {@link IListViewModelContract} so it can
 * instantiate RecyclerView Adapters.
 */
public final class WidgetConfigViewModel extends AndroidViewModel
        implements IListViewModelContract {

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final List<UserList> userLists;
    private final UserListsAdapter adapter;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final MutableLiveData<UserList> eventOpenUserList;
    private final MutableLiveData<String> eventDisplayError;

    public WidgetConfigViewModel(@NonNull Application application, IModelContract model) {
        super(application);
        this.model = model;
        disposable = new CompositeDisposable();
        userLists = new ArrayList<>();
        adapter = new UserListsAdapter(this, null);
        eventDisplayLoading = new MutableLiveData<>();
        eventOpenUserList = new MutableLiveData<>();
        eventDisplayError = new MutableLiveData<>();

        init();
    }

    private void init() {
        eventDisplayLoading.setValue(true);
        getAllUserLists();
    }


    private void getAllUserLists() {
        disposable.add(model.getAllLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(userListsSubscriber())
        );
    }

    private DisposableSubscriber<List<UserList>> userListsSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> userLists) {
                updateUserList(userLists);
                updateUi();
            }

            @Override
            public void onError(Throwable t) {
                Timber.e(t);
                eventDisplayError.setValue(
                        getStringResource(R.string.error_msg_generic)
                );
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void updateUserList(List<UserList> userLists) {
        Timber.d("updateUserList");
        this.userLists.clear();
        this.userLists.addAll(userLists);
    }

    private void updateUi() {
        if (userLists.isEmpty()) {
            eventDisplayError.setValue(
                    getStringResource(R.string.error_msg_no_user_lists)
            );
        } else {
            adapter.swapData(userLists);
            eventDisplayLoading.setValue(false);
        }
    }


    @Override
    public void userListClicked(UserList userList) {
        eventOpenUserList.setValue(userList);
    }


    @Override
    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    @Override
    public LiveData<Boolean> getEventDisplayLoading() {
        return eventDisplayLoading;
    }

    @Override
    public LiveData<UserList> getEventOpenUserList() {
        return eventOpenUserList;
    }

    @Override
    public LiveData<String> getEventDisplayError() {
        return eventDisplayError;
    }



    private String getStringResource(int stringResId) {
        return getApplication().getString(stringResId);
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }


    // UNSUPPORTED METHODS

    @Override
    public void addButtonClicked() {
        unsupportedMethodException();
    }

    @Override
    public void add(String title) {
        unsupportedMethodException();
    }

    @Override
    public void dragging(int fromPosition, int toPosition) {
        unsupportedMethodException();
    }

    @Override
    public void movePermanently(int newPosition) {
        unsupportedMethodException();
    }

    @Override
    public void edit(int position) {
        unsupportedMethodException();
    }

    @Override
    public void changeTitle(int id, String newTitle) {
        unsupportedMethodException();
    }

    @Override
    public void delete(int position) {
        unsupportedMethodException();
    }

    @Override
    public void swipedLeft(int position) {
        unsupportedMethodException();
    }

    @Override
    public void undoRecentDeletion() {
        unsupportedMethodException();
    }

    @Override
    public void deletionNotificationTimedOut() {
        unsupportedMethodException();
    }

    @Override
    public void refresh() {
        unsupportedMethodException();
    }

    @Override
    public ItemTouchHelper getItemTouchHelper() {
        unsupportedMethodException();
        return null;
    }

    @Override
    public LiveData<String> getToolbarTitle() {
        unsupportedMethodException();
        return null;
    }

    @Override
    public LiveData<String> getEventNotifyUserOfDeletion() {
        unsupportedMethodException();
        return null;
    }

    @Override
    public LiveData<String> getEventAdd() {
        unsupportedMethodException();
        return null;
    }

    @Override
    public LiveData<EditingInfo> getEventEdit() {
        unsupportedMethodException();
        return null;
    }


    private void unsupportedMethodException() {
        throw new UnsupportedOperationException(
                "This method is not supported by " + WidgetConfigViewModel.class.getSimpleName()
        );
    }
}
