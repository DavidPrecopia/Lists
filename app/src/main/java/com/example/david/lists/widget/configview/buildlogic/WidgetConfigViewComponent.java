package com.example.david.lists.widget.configview.buildlogic;

import android.app.Application;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.widget.configview.IWidgetConfigContract;
import com.example.david.lists.widget.configview.WidgetConfigView;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        WidgetConfigViewModule.class,
        ViewCommonModule.class
})
public interface WidgetConfigViewComponent {
    String SHARED_PREFS = "widget_shared_prefs";

    void inject(WidgetConfigView view);

    @Component.Builder
    interface Builder {
        WidgetConfigViewComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder view(IWidgetConfigContract.View view);
    }
}
