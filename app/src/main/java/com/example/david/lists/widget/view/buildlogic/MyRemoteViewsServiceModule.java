package com.example.david.lists.widget.view.buildlogic;

import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

import com.example.david.lists.R;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.widget.view.MyRemoteViewsFactory;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
final class MyRemoteViewsServiceModule {
    @Provides
    RemoteViewsService.RemoteViewsFactory remoteViewsFactory(Application application,
                                                             String userListId,
                                                             IRepositoryContract.Repository repository,
                                                             CompositeDisposable disposable,
                                                             AppWidgetManager appWidgetManager,
                                                             int appWidgetId) {
        return new MyRemoteViewsFactory(application, userListId, repository, disposable, appWidgetManager, appWidgetId);
    }

    @Provides
    String userListId(Intent intent, Application application) {
        return intent.getStringExtra(application.getString(R.string.intent_extra_user_list_id));
    }

    @Provides
    AppWidgetManager appWidgetManager(Application application) {
        return AppWidgetManager.getInstance(application);
    }

    @Provides
    int appWidgetId(Intent intent) {
        return intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }
}
