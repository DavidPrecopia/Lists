package com.example.david.lists.view.addedit.common.buildlogic;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.util.UtilSoftKeyboard;

import dagger.Module;
import dagger.Provides;

@Module
public final class AddEditDialogFragmentCommonModule {
    @ViewScope
    @Provides
    UtilSoftKeyboard utilSoftKeyboard() {
        return new UtilSoftKeyboard();
    }
}
