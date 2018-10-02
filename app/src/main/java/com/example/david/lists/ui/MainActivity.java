package com.example.david.lists.ui;

import android.os.Bundle;

import com.example.david.lists.R;
import com.example.david.lists.databinding.ActivityMainBinding;
import com.example.david.lists.ui.detail.DetailFragment;
import com.example.david.lists.ui.list.ListFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity
        implements ListFragment.ListFragmentClickListener {

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init(savedInstanceState == null);
    }

    private void init(boolean newActivity) {
        fragmentManager = getSupportFragmentManager();
        if (newActivity) {
            // Because this is the first Fragment, do not add to the backstack
            fragmentManager.beginTransaction()
                    .add(binding.fragmentHolder.getId(), ListFragment.newInstance())
                    .commit();
        }
    }


    @Override
    public void openDetailFragment(int listId, String listTitle) {
        replaceFragment(DetailFragment.newInstance(listId, listTitle));
    }


    private void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(binding.fragmentHolder.getId(), fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


    /**
     * @return true if Up navigation completed successfully <i>and</i> this Activity was finished, false otherwise.
     */
    @Override
    public boolean onSupportNavigateUp() {
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
            return false;
        } else {
            return super.onSupportNavigateUp();
        }
    }
}
