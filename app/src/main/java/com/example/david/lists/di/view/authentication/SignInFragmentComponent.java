package com.example.david.lists.di.view.authentication;

import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.view.authentication.SignInFragment;

import dagger.Component;

@ViewScope
@Component(modules = {SignInFragmentModule.class})
public interface SignInFragmentComponent {
    void inject(SignInFragment fragment);
}
