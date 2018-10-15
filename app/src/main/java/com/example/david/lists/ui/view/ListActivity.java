package com.example.david.lists.ui.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.david.lists.R;
import com.example.david.lists.databinding.ActivityMainBinding;
import com.example.david.lists.datamodel.UserList;
import com.example.david.lists.ui.viewmodels.IViewModelContract;
import com.example.david.lists.ui.viewmodels.UtilListViewModels;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.david.lists.util.UtilWidgetKeys.getIntentBundleName;
import static com.example.david.lists.util.UtilWidgetKeys.getIntentKeyId;
import static com.example.david.lists.util.UtilWidgetKeys.getIntentKeyTitle;

public class ListActivity extends AppCompatActivity {

    private IViewModelContract viewModel;

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
        initViewModel();
        observeViewModel();
        if (getIntent().getExtras() != null) {
            processIntentExtras(getIntent().getExtras());
            newActivity = false;
        }
        if (newActivity) {
            addFragment(ListFragment.newInstance(getString(R.string.displaying_user_list)));
        }
    }

    private void initViewModel() {
        viewModel = UtilListViewModels.getUserListViewModel(this, getApplication());
    }

    private void observeViewModel() {
        viewModel.getEventOpenUserList().observe(this, this::openUserList);
    }


    private void openUserList(UserList userList) {
        saveUserListDetails(userList.getId(), userList.getTitle());
        addFragmentToBackStack(
                ListFragment.newInstance(getString(R.string.displaying_item))
        );
    }

    private void saveUserListDetails(int id, String title) {
        SharedPreferences.Editor editor =
                getSharedPreferences(getString(R.string.key_shared_prefs_name), MODE_PRIVATE).edit();
        editor.putInt(getString(R.string.key_shared_pref_user_list_id), id);
        editor.putString(getString(R.string.key_shared_pref_user_list_title), title);
        editor.apply();
    }


    private void addFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .add(binding.fragmentHolder.getId(), fragment)
                .commit();
    }

    private void addFragmentToBackStack(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(binding.fragmentHolder.getId(), fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }


    private void processIntentExtras(Bundle intentExtras) {
        Bundle widgetBundle = intentExtras.getBundle(getIntentBundleName(getApplicationContext()));
        if (widgetBundle != null) {
            processWidgetBundle(widgetBundle);
        }
    }

    private void processWidgetBundle(Bundle widgetBundle) {
        saveUserListDetails(
                widgetBundle.getInt(getIntentKeyId(getApplicationContext())),
                widgetBundle.getString(getIntentKeyTitle(getApplicationContext()))
        );
        addFragment(
                ListFragment.newInstance(getString(R.string.displaying_item))
        );
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getExtras() != null) {
            processIntentExtras(intent.getExtras());
        }
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
