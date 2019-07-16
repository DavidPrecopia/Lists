package com.example.david.lists.view.common;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import javax.inject.Inject;

/**
 * To be used by an Activity that is backing a Fragment/View.
 */
public abstract class ActivityBase extends AppCompatActivity {
    @Inject
    protected FragmentManager fragmentManager;

    protected void addFragment(Fragment fragment, int containerViewId) {
        fragmentManager.beginTransaction()
                .add(containerViewId, fragment)
                .commit();
    }

    protected void openDialogFragment(DialogFragment dialogFragment) {
        dialogFragment.show(fragmentManager, null);
    }

    // TODO Do I still need this?
    protected void removeAllFragments() {
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss();
        }
    }

    // TODO Do I still need this?
    protected void toastMessage(int message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
