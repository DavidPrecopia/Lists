package com.example.david.lists.ui.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
import com.example.david.lists.databinding.ActivityMainBinding;
import com.example.david.lists.di.view.DaggerMainActivityComponent;
import com.example.david.lists.util.UtilExceptions;
import com.example.david.lists.util.UtilUser;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import javax.inject.Inject;
import javax.inject.Provider;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import static com.example.david.lists.util.UtilWidgetKeys.getIntentBundleName;
import static com.example.david.lists.util.UtilWidgetKeys.getIntentKeyId;
import static com.example.david.lists.util.UtilWidgetKeys.getIntentKeyTitle;

public class MainActivity extends AppCompatActivity
        implements UserListsFragment.UserListsFragmentListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityMainBinding binding;

    @Inject
    FragmentManager fragmentManager;
    @Inject
    SharedPreferences sharedPrefs;

    @Inject
    Provider<Intent> authenticationIntent;
    private static final int RESPONSE_CODE_AUTH = 100;

    private boolean newActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inject();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        verifyUser(savedInstanceState == null);
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


    private void inject() {
        DaggerMainActivityComponent.builder()
                .mainActivity(this)
                .build()
                .inject(this);
    }

    private void verifyUser(boolean newActivity) {
        initFields(newActivity);
        if (UtilUser.signedOut()) {
            openAuthentication();
        } else {
            initLayout();
        }
    }

    private void initFields(boolean newActivity) {
        this.newActivity = newActivity;
    }

    private void initLayout() {
        showFragment();
        initView();
    }

    private void initView() {
        if (getIntent().getExtras() != null) {
            processIntentExtras(getIntent().getExtras());
        }
        if (newActivity) {
            addFragment(getUserListsFragment());
        }
    }

    private void processIntentExtras(Bundle intentExtras) {
        Bundle widgetBundle = intentExtras.getBundle(getIntentBundleName(getApplicationContext()));
        if (widgetBundle != null) {
            processWidgetBundle(widgetBundle);
            newActivity = false;
        }
    }

    private void processWidgetBundle(Bundle widgetBundle) {
        addFragment(getItemsFragment(
                widgetBundle.getString(getIntentKeyId(getApplicationContext())),
                widgetBundle.getString(getIntentKeyTitle(getApplicationContext()))
        ));
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


    private UserListsFragment getUserListsFragment() {
        return UserListsFragment.newInstance();
    }

    private ItemsFragment getItemsFragment(String groupId, String title) {
        return ItemsFragment.newInstance(groupId, title);
    }


    private void showFragment() {
        binding.progressBar.setVisibility(View.GONE);
        binding.fragmentHolder.setVisibility(View.VISIBLE);
    }

    private void hideFragment() {
        binding.fragmentHolder.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void messages(int message) {
        switch (message) {
            case UserListsFragment.UserListsFragmentListener.SIGN_OUT:
                signOut();
                break;
            case UserListsFragment.UserListsFragmentListener.SIGN_IN:
                signIn();
                break;
            default:
                UtilExceptions.throwException(new IllegalArgumentException());
        }
    }

    @Override
    public void openGroup(UserList userList) {
        addFragmentToBackStack(getItemsFragment(userList.getId(), userList.getTitle()));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESPONSE_CODE_AUTH) {
            processAuthResult(IdpResponse.fromResultIntent(data), resultCode);
        }
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.night_mode_shared_pref_key))) {
            recreate();
        }
    }


    private void openAuthentication() {
        hideFragment();
        startActivityForResult(
                authenticationIntent.get(),
                RESPONSE_CODE_AUTH
        );
    }

    @SuppressLint("RestrictedApi")
    private void processAuthResult(IdpResponse response, int resultCode) {
        if (resultCode == RESULT_OK) {
            toastMessage(R.string.msg_welcome_user);
            initLayout();
            return;
        }

        if (response == null) {
            toastMessage(R.string.msg_sign_in_cancelled);
        } else {
            toastMessage(ErrorCodes.toFriendlyMessage(
                    response.getError().getErrorCode()
            ));
        }
        finish();
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
        verifyUser(true);
    }

    private void failedToSignOut(Exception e) {
        UtilExceptions.throwException(e);
        toastMessage(R.string.error_experienced_signing_out);
    }


    private void signIn() {
        fragmentManager.popBackStack();
        openAuthentication();
    }


    private void toastMessage(int stringResId) {
        Toast.makeText(getApplicationContext(), stringResId, Toast.LENGTH_SHORT).show();
    }

    private void toastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
