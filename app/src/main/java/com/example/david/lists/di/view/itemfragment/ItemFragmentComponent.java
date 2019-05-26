package com.example.david.lists.di.view.itemfragment;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.di.view.common.RecyclerViewAdapterModule;
import com.example.david.lists.di.view.common.ViewCommonModule;
import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.common.TouchHelperCallback;
import com.example.david.lists.view.itemlist.ItemFragment;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        ItemFragmentModule.class,
        RecyclerViewAdapterModule.class,
        ViewCommonModule.class
})
public interface ItemFragmentComponent {
    void inject(ItemFragment itemFragment);

    @Component.Builder
    interface Builder {
        ItemFragmentComponent build();

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
