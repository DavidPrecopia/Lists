package com.example.david.lists.view.authentication.buildlogic;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.authentication.SignInFragment;

import dagger.Component;

@ViewScope
@Component(modules = {SignInFragmentModule.class})
public interface SignInFragmentComponent {
    void inject(SignInFragment fragment);
}
