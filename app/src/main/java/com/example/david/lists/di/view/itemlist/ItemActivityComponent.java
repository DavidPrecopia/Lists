package com.example.david.lists.di.view.itemlist;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.di.view.common.ActivityCommonModule;
import com.example.david.lists.di.view.common.ViewCommonModule;
import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.itemlist.ItemActivity;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        ActivityCommonModule.class,
        ViewCommonModule.class
})
public interface ItemActivityComponent {
    void inject(ItemActivity activity);

    @Component.Builder
    interface Builder {
        ItemActivityComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder activity(AppCompatActivity appCompatActivity);
    }
}
