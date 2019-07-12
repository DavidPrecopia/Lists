package com.example.david.lists.view.itemlist.buldlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.common.TouchHelperCallback;
import com.example.david.lists.view.itemlist.ItemFragment;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        ItemFragmentModule.class,
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
