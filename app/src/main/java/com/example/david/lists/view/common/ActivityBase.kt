package com.example.david.lists.view.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import javax.inject.Inject

abstract class ActivityBase : AppCompatActivity() {
    @Inject
    lateinit var fragmentManager: FragmentManager

    protected fun addFragment(fragment: Fragment, containerViewId: Int) {
        fragmentManager.commit {
            add(containerViewId, fragment)
        }
    }

    protected fun removeAllFragments() {
        for (fragment in fragmentManager.fragments) {
            fragmentManager.commit(allowStateLoss = true) {
                remove(fragment)
            }
        }
    }
}
