package com.example.david.lists.di.view.addeditfragment;

import com.example.david.lists.di.view.common.ViewScope;
import com.example.david.lists.util.UtilSoftKeyboard;

import dagger.Module;
import dagger.Provides;

@Module
public final class AddEditFragmentCommonModule {
    @ViewScope
    @Provides
    UtilSoftKeyboard utilSoftKeyboard() {
        return new UtilSoftKeyboard();
    }
}
