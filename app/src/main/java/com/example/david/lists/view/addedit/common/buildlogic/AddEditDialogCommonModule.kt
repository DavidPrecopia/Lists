package com.example.david.lists.view.addedit.common.buildlogic

import android.app.Application
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.util.UtilSoftKeyboard
import com.example.david.lists.view.addedit.common.AddEditViewModel
import com.example.david.lists.view.addedit.common.IAddEditContract
import dagger.Module
import dagger.Provides

@Module
class AddEditDialogCommonModule {
    @ViewScope
    @Provides
    fun utilSoftKeyboard(inputMethodManager: InputMethodManager): UtilSoftKeyboard {
        return UtilSoftKeyboard(inputMethodManager)
    }

    @ViewScope
    @Provides
    fun inputMethodManager(application: Application): InputMethodManager {
        return application.getSystemService<InputMethodManager>()!!
    }

    @ViewScope
    @Provides
    fun viewModel(application: Application): IAddEditContract.ViewModel {
        return AddEditViewModel(application)
    }
}
