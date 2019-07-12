package com.example.david.lists.widget.configview.buildlogic;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.widget.configview.WidgetConfigView;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        WidgetConfigViewModule.class,
        ViewCommonModule.class
})
public interface WidgetConfigViewComponent {
    void inject(WidgetConfigView activity);

    @Component.Builder
    interface Builder {
        WidgetConfigViewComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder activity(AppCompatActivity appCompatActivity);

        @BindsInstance
        Builder widgetId(int widgetId);
    }
}
