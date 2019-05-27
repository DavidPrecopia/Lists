package com.example.david.lists.view.userlistlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IUserRepository;
import com.example.david.lists.databinding.ActivityUserListBinding;
import com.example.david.lists.di.view.userlistfragment.DaggerUserListActivityComponent;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.authentication.SignInFragment;
import com.example.david.lists.view.common.ActivityBase;
import com.example.david.lists.view.itemlist.ItemActivity;
import com.firebase.ui.auth.AuthUI;

import javax.inject.Inject;

public class UserListActivity extends ActivityBase
        implements UserListFragment.UserListsFragmentListener,
        SignInFragment.SignInFragmentCallback,
        SharedPreferences.OnSharedPreferenceChangeListener{

    private ActivityUserListBinding binding;

    @Inject
    SharedPreferences sharedPrefs;

    @Inject
    IUserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inject();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_list);
        init();
    }

    private void inject() {
        DaggerUserListActivityComponent.builder()
                .application(getApplication())
                .activity(this)
                .build()
                .inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }


    private void init() {
        if (userRepository.signedOut()) {
            openAuthentication();
        } else {
            initView();
        }
    }

    private void openAuthentication() {
        addFragment(SignInFragment.getInstance(), binding.fragmentHolder.getId());
    }

    private void initView() {
        binding.progressBar.setVisibility(View.GONE);
        if (newActivity) {
            addFragment(UserListFragment.newInstance(), binding.fragmentHolder.getId());
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.night_mode_shared_pref_key))) {
            recreate();
        }
    }


    @Override
    public void successfullySignedIn() {
        initView();
    }

    @Override
    public void openUserList(UserList userList) {
        Bundle bundle = new Bundle();
        startActivity(getOpenUserListIntent(userList), bundle);
    }

    private Intent getOpenUserListIntent(UserList userList) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra(getString(R.string.intent_extra_user_list_id), userList.getId());
        intent.putExtra(getString(R.string.intent_extra_user_list_title), userList.getTitle());
        return intent;
    }


    @Override
    public void messages(int message) {
        switch (message) {
            case UserListFragment.UserListsFragmentListener.SIGN_OUT:
                signOut();
                break;
            case UserListFragment.UserListsFragmentListener.SIGN_IN:
                signIn();
                break;
            default:
                UtilExceptions.throwException(new IllegalArgumentException());
        }
    }

    private void signOut() {
        AuthUI.getInstance().signOut(this)
                .addOnSuccessListener(aVoid -> successfullySignedOut())
                .addOnFailureListener(this::failedToSignOut);
    }

    private void successfullySignedOut() {
        toastMessage(R.string.msg_successful_signed_out);
        for (Fragment fragment : fragmentManager.getFragments()) {
            fragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss();
        }
        init();
    }

    private void failedToSignOut(Exception e) {
        UtilExceptions.throwException(e);
        toastMessage(R.string.error_experienced_signing_out);
    }

    private void signIn() {
        fragmentManager.popBackStack();
        openAuthentication();
    }


    private void toastMessage(int message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
