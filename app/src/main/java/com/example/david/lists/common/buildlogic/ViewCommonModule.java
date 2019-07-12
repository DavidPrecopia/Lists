package com.example.david.lists.common.buildlogic;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.common.ListsApplication;
import com.example.david.lists.data.repository.IRepository;
import com.example.david.lists.data.repository.IUserRepository;
import com.example.david.lists.view.common.TouchHelperCallback;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public final class ViewCommonModule {
    @Provides
    IRepository repository(AppComponent appComponent) {
        return appComponent.repository();
    }

    @Provides
    IUserRepository userRepository(AppComponent appComponent) {
        return appComponent.userRepository();
    }

    @Provides
    SharedPreferences sharedPreferences(AppComponent appComponent) {
        return appComponent.sharedPrefsNightMode();
    }

    @Provides
    AppComponent appComponent(Application application) {
        return ((ListsApplication) application).getAppComponent();
    }

    @ViewScope
    @Provides
    CompositeDisposable disposable() {
        return new CompositeDisposable();
    }


    /**
     * RecyclerView
     */
    @Provides
    LinearLayoutManager layoutManager(Application application) {
        return new LinearLayoutManager(application.getApplicationContext());
    }

    @ViewScope
    @Provides
    ItemTouchHelper itemTouchHelper(TouchHelperCallback.MovementCallback movementCallback) {
        return new ItemTouchHelper(new TouchHelperCallback(movementCallback));
    }

    @ViewScope
    @Provides
    RecyclerView.ItemDecoration itemDecoration(Application application, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(application.getApplicationContext(), layoutManager.getOrientation());
    }

    @ViewScope
    @Provides
    ViewBinderHelper viewBinderHelper() {
        ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
        viewBinderHelper.setOpenOnlyOne(true);
        return viewBinderHelper;
    }
}
