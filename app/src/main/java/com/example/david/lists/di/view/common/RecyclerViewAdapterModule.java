package com.example.david.lists.di.view.common;

import android.app.Application;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.david.lists.view.common.TouchHelperCallback;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;

@Module
public final class RecyclerViewAdapterModule {
    // If this is scoped then the same instance is injected.
    @Provides
    LinearLayoutManager layoutManager(Application application) {
        return new LinearLayoutManager(application.getApplicationContext());
    }

    @Reusable
    @Provides
    ItemTouchHelper itemTouchHelper(TouchHelperCallback.MovementCallback movementCallback) {
        return new ItemTouchHelper(new TouchHelperCallback(movementCallback));
    }

    @Reusable
    @Provides
    RecyclerView.ItemDecoration itemDecoration(Application application, LinearLayoutManager layoutManager) {
        return new DividerItemDecoration(application.getApplicationContext(), layoutManager.getOrientation());
    }

    @Reusable
    @Provides
    ViewBinderHelper viewBinderHelper() {
        ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
        viewBinderHelper.setOpenOnlyOne(true);
        return viewBinderHelper;
    }
}