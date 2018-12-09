package com.example.david.lists.widget.configactivity;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.data.model.IModelContract;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.widget.WidgetRemoteView;

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

import static android.content.Context.MODE_PRIVATE;
import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefKeyId;
import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefKeyTitle;
import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefName;

public final class WidgetConfigViewModel extends AndroidViewModel
        implements IWidgetConfigViewModelContract {

    private final IModelContract model;
    private final CompositeDisposable disposable;

    private final List<Group> groups;
    private final WidgetConfigAdapter adapter;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final SingleLiveEvent<Void> eventSuccessful;
    private final MutableLiveData<String> eventDisplayError;
    private final int widgetId;

    WidgetConfigViewModel(@NonNull Application application, IModelContract model, int widgetId) {
        super(application);
        this.model = model;
        this.widgetId = widgetId;
        disposable = new CompositeDisposable();
        groups = new ArrayList<>();
        adapter = new WidgetConfigAdapter(this);
        eventDisplayLoading = new MutableLiveData<>();
        eventSuccessful = new SingleLiveEvent<>();
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
        saveDetails(group.getId(), group.getTitle());
        updateWidget();
        eventSuccessful.call();
    }

    private void saveDetails(String id, String title) {
        SharedPreferences.Editor editor = getApplication().getSharedPreferences(
                getSharedPrefName(getApplication()), MODE_PRIVATE
        ).edit();
        editor.putString(getSharedPrefKeyId(getApplication(), widgetId), id);
        editor.putString(getSharedPrefKeyTitle(getApplication(), widgetId), title);
        editor.apply();
    }

    private void updateWidget() {
        RemoteViews remoteView = new WidgetRemoteView(getApplication(), widgetId).updateWidget();
        AppWidgetManager.getInstance(getApplication()).updateAppWidget(widgetId, remoteView);
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
    public LiveData<Void> getEventSuccessful() {
        return eventSuccessful;
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
