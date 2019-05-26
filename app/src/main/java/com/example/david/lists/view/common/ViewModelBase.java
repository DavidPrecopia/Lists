package com.example.david.lists.view.common;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.david.lists.R;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.util.SingleLiveEvent;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public abstract class ViewModelBase extends AndroidViewModel {

    protected final IRepository repository;
    protected final CompositeDisposable disposable;

    protected final MutableLiveData<Boolean> eventDisplayLoading;
    protected final SingleLiveEvent<Boolean> eventDisplayError;
    protected final SingleLiveEvent<String> errorMessage;
    protected final SingleLiveEvent<String> eventNotifyUserOfDeletion;
    protected final SingleLiveEvent<String> eventAdd;

    public ViewModelBase(@NonNull Application application, IRepository repository) {
        super(application);
        this.repository = repository;
        disposable = new CompositeDisposable();
        eventDisplayLoading = new MutableLiveData<>();
        eventDisplayError = new SingleLiveEvent<>();
        errorMessage = new SingleLiveEvent<>();
        eventNotifyUserOfDeletion = new SingleLiveEvent<>();
        eventAdd = new SingleLiveEvent<>();
        init();
    }

    private void init() {
        eventDisplayLoading.setValue(true);
    }

    
    protected void evaluateNewData(List newList) {
        eventDisplayLoading.setValue(false);

        if (newList.isEmpty()) {
            errorMessage.setValue(getStringResource(R.string.error_msg_empty_user_list));
            eventDisplayError.setValue(true);
        } else {
            eventDisplayError.setValue(false);
        }
    }

    protected String getStringResource(int resId) {
        return getApplication().getString(resId);
    }

    protected String getStringResource(int resId, Object object) {
        return getApplication().getString(resId, object);
    }
}
