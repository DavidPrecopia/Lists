package com.precopia.david.lists.view.addedit.common.buildlogic

import android.app.Application
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import com.precopia.david.lists.common.buildlogic.ViewScope
import com.precopia.david.lists.util.UtilSoftKeyboard
import com.precopia.david.lists.view.addedit.common.AddEditViewModel
import com.precopia.david.lists.view.addedit.common.IAddEditContract
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
        return application.getSystemService()!!
    }

    @JvmSuppressWildcards
    @ViewScope
    @Provides
    fun viewModel(getStringRes: (Int) -> String): IAddEditContract.ViewModel {
        return AddEditViewModel(getStringRes)
    }
}
