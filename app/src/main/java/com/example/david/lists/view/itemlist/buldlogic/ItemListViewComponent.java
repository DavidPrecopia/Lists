package com.example.david.lists.view.itemlist.buldlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.common.TouchHelperCallback;
import com.example.david.lists.view.itemlist.ItemListView;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        ItemListViewModule.class,
        ViewCommonModule.class
})
public interface ItemListViewComponent {
    void inject(ItemListView itemFragment);

    @Component.Builder
    interface Builder {
        ItemListViewComponent build();

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
