package com.example.david.lists.view.itemlist.buldlogic;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.common.buildlogic.ActivityCommonModule;
import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
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
