package com.example.david.lists.di.view;

import android.app.Application;

import com.example.david.lists.ui.view.ItemsFragment;

import dagger.BindsInstance;
import dagger.Component;

@ItemsFragmentScope
@Component(modules = {ItemsFragmentModule.class})
public interface ItemsFragmentComponent {

    void inject(ItemsFragment itemsFragment);

    @Component.Builder
    interface Builder {
        ItemsFragmentComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder userListId(String userListId);
    }
}
