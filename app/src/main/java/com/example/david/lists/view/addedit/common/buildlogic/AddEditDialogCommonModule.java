package com.example.david.lists.view.addedit.common.buildlogic;

import android.app.Application;

import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.util.UtilSoftKeyboard;
import com.example.david.lists.view.addedit.common.AddEditViewModel;
import com.example.david.lists.view.addedit.common.IAddEditContract;

import dagger.Module;
import dagger.Provides;

@Module
public final class AddEditDialogCommonModule {
    @ViewScope
    @Provides
    UtilSoftKeyboard utilSoftKeyboard() {
        return new UtilSoftKeyboard();
    }

    @ViewScope
    @Provides
    IAddEditContract.ViewModel viewModel(Application application) {
        return new AddEditViewModel(application);
    }
}
