package com.example.david.lists.ui.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.david.lists.BuildConfig;
import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.Group;
import com.example.david.lists.databinding.ActivityMainBinding;
import com.example.david.lists.util.UtilUser;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import timber.log.Timber;

import static com.example.david.lists.util.UtilWidgetKeys.getIntentBundleName;
import static com.example.david.lists.util.UtilWidgetKeys.getIntentKeyId;
import static com.example.david.lists.util.UtilWidgetKeys.getIntentKeyTitle;

public class MainActivity extends AppCompatActivity
        implements GroupsFragment.GroupFragmentListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private static final int RESPONSE_CODE_AUTH = 100;

    private boolean newActivity;

    @Override
    protected void onResume() {
        super.onResume();
        getSharedPreferences(getString(R.string.night_mode_shared_pref_name), MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getSharedPreferences(getString(R.string.night_mode_shared_pref_name), MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        verifyUser(savedInstanceState == null);
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
        fragmentManager = getSupportFragmentManager();
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
            addFragment(getGroupFragment());
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


    private GroupsFragment getGroupFragment() {
        return GroupsFragment.newInstance();
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
            case GroupsFragment.GroupFragmentListener.SIGN_OUT:
                signOut();
                break;
            case GroupsFragment.GroupFragmentListener.SIGN_IN:
                signIn();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void openGroup(Group group) {
        addFragmentToBackStack(getItemsFragment(group.getId(), group.getTitle()));
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
                getAuthIntent(),
                RESPONSE_CODE_AUTH
        );
    }

    private Intent getAuthIntent() {
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(getProviders())
                .enableAnonymousUsersAutoUpgrade()
                .setIsSmartLockEnabled(false, true)
                .setLogo(R.mipmap.ic_launcher_round)
                .setTheme(R.style.FirebaseUIAuthStyle)
                .build();
    }

    private List<AuthUI.IdpConfig> getProviders() {
        return Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build()
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
            fragmentManager.beginTransaction().remove(fragment).commit();
        }
        verifyUser(true);
    }

    private void failedToSignOut(Exception e) {
        if (BuildConfig.DEBUG) Timber.e(e);
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
