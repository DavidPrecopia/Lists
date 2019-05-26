package com.example.david.lists.di.view.mainactivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.david.lists.di.view.common.ViewScope;

import dagger.Module;
import dagger.Provides;

@Module
final class MainActivityModule {
    @ViewScope
    @Provides
    FragmentManager fragmentManager(AppCompatActivity activity) {
        return activity.getSupportFragmentManager();
    }
}
