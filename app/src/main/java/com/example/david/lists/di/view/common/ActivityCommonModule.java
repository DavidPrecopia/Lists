package com.example.david.lists.di.view.common;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import dagger.Module;
import dagger.Provides;

@Module
public final class ActivityCommonModule {
    @ViewScope
    @Provides
    FragmentManager fragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }
}
