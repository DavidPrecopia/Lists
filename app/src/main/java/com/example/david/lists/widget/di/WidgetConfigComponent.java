package com.example.david.lists.widget.di;

import android.app.Application;

import com.example.david.lists.widget.configactivity.WidgetConfigActivity;

import dagger.BindsInstance;
import dagger.Component;

@WidgetConfigScope
@Component(modules = {WidgetConfigModule.class})
public interface WidgetConfigComponent {
    void inject(WidgetConfigActivity activity);

    @Component.Builder
    interface Builder {
        WidgetConfigComponent build();

        @BindsInstance
        Builder widgetConfigActivity(WidgetConfigActivity widgetConfigActivity);

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder widgetId(int widgetId);
    }
}
