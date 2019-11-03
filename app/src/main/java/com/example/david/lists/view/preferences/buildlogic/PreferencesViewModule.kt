package com.example.david.lists.view.preferences.buildlogic

import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.preferences.IPreferencesViewContract
import com.example.david.lists.view.preferences.PreferencesLogic
import dagger.Module
import dagger.Provides

@Module
class PreferencesViewModule {
    @ViewScope
    @Provides
    fun logic(view: IPreferencesViewContract.View): IPreferencesViewContract.Logic {
        return PreferencesLogic(view)
    }
}