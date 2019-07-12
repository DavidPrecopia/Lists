package com.example.david.lists.widget.configactivity.buildlogic;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.widget.configactivity.WidgetConfigActivity;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        WidgetConfigModule.class,
        ViewCommonModule.class
})
public interface WidgetConfigComponent {
    void inject(WidgetConfigActivity activity);

    @Component.Builder
    interface Builder {
        WidgetConfigComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder activity(AppCompatActivity appCompatActivity);

        @BindsInstance
        Builder widgetId(int widgetId);
    }
}
