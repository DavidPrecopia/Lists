package com.example.david.lists.ui;

import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.david.lists.R;
import com.example.david.lists.databinding.ActivityMainBinding;

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
            // Because this is the first Fragment,
            // do not add to the backstack
            fragmentManager.beginTransaction()
                    .add(binding.fragmentHolder.getId(), ListFragment.newInstance())
                    .commit();
        }
    }


    @Override
    public void openDetailFragment(int listId) {
        addFragment(DetailFragment.newInstance(listId));
    }


    private void addFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .add(binding.fragmentHolder.getId(), fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}
