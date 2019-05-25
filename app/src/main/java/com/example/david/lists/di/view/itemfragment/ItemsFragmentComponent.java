package com.example.david.lists.di.view.itemfragment;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.di.view.RecyclerViewAdapterModule;
import com.example.david.lists.di.view.ViewScope;
import com.example.david.lists.ui.common.TouchHelperCallback;
import com.example.david.lists.ui.itemlist.ItemsFragment;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {ItemsFragmentModule.class, RecyclerViewAdapterModule.class})
public interface ItemsFragmentComponent {
    void inject(ItemsFragment itemsFragment);

    @Component.Builder
    interface Builder {
        ItemsFragmentComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder movementCallback(TouchHelperCallback.MovementCallback movementCallback);

        @BindsInstance
        Builder userListId(String userListId);
    }
}