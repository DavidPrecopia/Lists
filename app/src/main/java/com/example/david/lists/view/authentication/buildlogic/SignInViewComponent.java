package com.example.david.lists.view.authentication.buildlogic;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.authentication.SignInView;

import dagger.Component;

@ViewScope
@Component(modules = {SignInViewModule.class})
public interface SignInViewComponent {
    void inject(SignInView fragment);
}
