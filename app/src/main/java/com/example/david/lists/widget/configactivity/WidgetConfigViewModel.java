package com.example.david.lists.widget.configactivity;

import android.app.Application;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.model.IModelContract;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import timber.log.Timber;

public final class WidgetConfigViewModel extends AndroidViewModel
        implements IWidgetConfigViewModelContract {

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final List<Group> groups;
    private final WidgetConfigAdapter adapter;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final MutableLiveData<Group> eventSelectedGroup;
    private final MutableLiveData<String> eventDisplayError;

    public WidgetConfigViewModel(@NonNull Application application, IModelContract model) {
        super(application);
        this.model = model;
        disposable = new CompositeDisposable();
        groups = new ArrayList<>();
        adapter = new WidgetConfigAdapter(this);
        eventDisplayLoading = new MutableLiveData<>();
        eventSelectedGroup = new MutableLiveData<>();
        eventDisplayError = new MutableLiveData<>();

        init();
    }

    private void init() {
        eventDisplayLoading.setValue(true);
        getGroups();
    }


    private void getGroups() {
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

    private void updateGroupList(List<Group> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }

    private void updateUi() {
        if (groups.isEmpty()) {
            eventDisplayError.setValue(
                    getStringResource(R.string.error_msg_no_groups)
            );
        } else {
            adapter.swapData(groups);
            eventDisplayLoading.setValue(false);
        }
    }


    @Override
    public void groupClicked(Group group) {
        eventSelectedGroup.setValue(group);
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
    public LiveData<Group> getEventSelectGroup() {
        return eventSelectedGroup;
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
}
