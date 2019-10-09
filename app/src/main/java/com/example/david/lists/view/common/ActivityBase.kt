package com.example.david.lists.view.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import javax.inject.Inject

abstract class ActivityBase(layoutRes: Int) : AppCompatActivity(layoutRes) {
    @Inject
    lateinit var fragmentManager: FragmentManager

    protected fun addFragment(fragment: Fragment, containerViewId: Int) {
        fragmentManager.commit {
            add(containerViewId, fragment)
        }
    }
}
