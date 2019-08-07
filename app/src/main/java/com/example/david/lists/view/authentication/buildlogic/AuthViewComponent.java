package com.example.david.lists.view.authentication.buildlogic;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

import com.example.david.lists.common.buildlogic.ActivityCommonModule;
import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.authentication.AuthView;
import com.example.david.lists.view.authentication.IAuthContract;

import dagger.BindsInstance;
import dagger.Component;

@ViewScope
@Component(modules = {
        AuthViewModule.class,
        ActivityCommonModule.class,
        ViewCommonModule.class
})
public interface AuthViewComponent {
    void inject(AuthView authView);

    @Component.Builder
    interface Builder {
        AuthViewComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder view(IAuthContract.View view);

        @BindsInstance
        Builder activity(AppCompatActivity activity);
    }
}
