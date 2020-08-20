package com.precopia.david.lists.common.buildlogic

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SharedPrefsModule {
    @Singleton
    @Provides
    fun sharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
                application.packageName + "_preferences",
                MODE_PRIVATE
        )
    }
}
