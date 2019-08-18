package com.example.david.lists.common.buildlogic

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

import dagger.Module
import dagger.Provides

@Module
class ActivityCommonModule {
    @ViewScope
    @Provides
    fun fragmentManager(activity: AppCompatActivity): FragmentManager {
        return activity.supportFragmentManager
    }
}
