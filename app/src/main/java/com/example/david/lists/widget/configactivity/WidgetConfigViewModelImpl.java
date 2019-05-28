package com.example.david.lists.widget.configactivity;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.util.SingleLiveEvent;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.widget.WidgetRemoteView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

import static android.content.Context.MODE_PRIVATE;
import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefKeyId;
import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefKeyTitle;
import static com.example.david.lists.util.UtilWidgetKeys.getSharedPrefName;

public final class WidgetConfigViewModelImpl extends AndroidViewModel
        implements IWidgetConfigViewModel {

    private final IRepository repository;
    private final CompositeDisposable disposable;

    private final int widgetId;
    private final MutableLiveData<List<UserList>> userLists;

    private final MutableLiveData<Boolean> eventDisplayLoading;
    private final SingleLiveEvent<Void> eventSuccessful;
    private final SingleLiveEvent<Boolean> eventDisplayError;
    private final SingleLiveEvent<String> errorMessage;

    WidgetConfigViewModelImpl(@NonNull Application application, IRepository repository, CompositeDisposable disposable, int widgetId) {
        super(application);
        this.repository = repository;
        this.disposable = disposable;
        this.widgetId = widgetId;
        userLists = new MutableLiveData<>();
        eventDisplayLoading = new MutableLiveData<>();
        eventSuccessful = new SingleLiveEvent<>();
        eventDisplayError = new SingleLiveEvent<>();
        errorMessage = new SingleLiveEvent<>();

        init();
    }

    private void init() {
        eventDisplayLoading.setValue(true);
        getUserListsFromRepository();
    }


    private void getUserListsFromRepository() {
        disposable.add(repository.getAllUserLists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(userListSubscriber())
        );
    }

    private DisposableSubscriber<List<UserList>> userListSubscriber() {
        return new DisposableSubscriber<List<UserList>>() {
            @Override
            public void onNext(List<UserList> userLists) {
                WidgetConfigViewModelImpl.this.userLists.setValue(userLists);
                evaluateNewData(userLists);
            }

            @Override
            public void onError(Throwable t) {
                UtilExceptions.throwException(t);
                errorMessage.setValue(getStringResource(R.string.error_msg_generic));
                eventDisplayError.setValue(true);
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void evaluateNewData(List<UserList> newUserListList) {
        eventDisplayLoading.setValue(false);

        if (newUserListList.isEmpty()) {
            errorMessage.setValue(getStringResource(R.string.error_msg_no_user_lists));
            eventDisplayError.setValue(true);
        } else {
            eventDisplayError.setValue(false);
        }
    }


    @Override
    public void userListClicked(UserList userList) {
        saveDetails(userList.getId(), userList.getTitle());
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
    public LiveData<List<UserList>> getUserLists() {
        List<UserList> value = userLists.getValue();
        if (value != null) {
            evaluateNewData(value);
        }
        return userLists;
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
    public LiveData<Boolean> getEventDisplayError() {
        return eventDisplayError;
    }

    @Override
    public LiveData<String> getErrorMessage() {
        return errorMessage;
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
