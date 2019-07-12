package com.example.david.lists.view.userlistlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.databinding.ActivityUserListBinding;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.view.authentication.ConfirmSignOutDialog;
import com.example.david.lists.view.authentication.SignInView;
import com.example.david.lists.view.common.ActivityBase;
import com.example.david.lists.view.itemlist.ItemActivity;
import com.example.david.lists.view.userlistlist.buldlogic.DaggerUserListActivityComponent;
import com.firebase.ui.auth.AuthUI;

import javax.inject.Inject;

public class UserListActivity extends ActivityBase
        implements UserListListView.UserListsFragmentListener,
        SignInView.SignInFragmentCallback,
        ConfirmSignOutDialog.ConfirmSignOutCallback,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityUserListBinding binding;

    @Inject
    SharedPreferences sharedPrefs;

    @Inject
    IRepositoryContract.UserRepository userRepository;

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
        addFragment(SignInView.getInstance(), binding.fragmentHolder.getId());
    }

    private void initView() {
        binding.progressBar.setVisibility(View.GONE);
        if (newActivity) {
            addFragment(UserListListView.newInstance(), binding.fragmentHolder.getId());
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
        // Need to reset this flag so the Fragment is added to the layout.
        this.newActivity = true;
        initView();
    }

    @Override
    public void openUserList(UserList userList) {
        startActivity(getOpenUserListIntent(userList));
    }

    private Intent getOpenUserListIntent(UserList userList) {
        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra(getString(R.string.intent_extra_user_list_id), userList.getId());
        intent.putExtra(getString(R.string.intent_extra_user_list_title), userList.getTitle());
        return intent;
    }


    @Override
    public void authMessage(int message) {
        switch (message) {
            case UserListListView.UserListsFragmentListener.SIGN_OUT:
                confirmSignOut();
                break;
            case UserListListView.UserListsFragmentListener.SIGN_IN:
                signIn();
                break;
            default:
                UtilExceptions.throwException(new IllegalArgumentException());
        }
    }

    private void confirmSignOut() {
        openDialogFragment(new ConfirmSignOutDialog());
    }

    @Override
    public void proceedWithSignOut() {
        AuthUI.getInstance().signOut(this)
                .addOnSuccessListener(aVoid -> successfullySignedOut())
                .addOnFailureListener(this::failedToSignOut);
    }

    private void successfullySignedOut() {
        toastMessage(R.string.msg_successful_signed_out);
        removeAllFragments();
        recreate();
    }

    private void failedToSignOut(Exception e) {
        UtilExceptions.throwException(e);
        toastMessage(R.string.error_experienced_signing_out);
    }

    private void signIn() {
        removeAllFragments();
        openAuthentication();
    }
}
