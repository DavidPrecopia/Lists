package com.example.david.lists.ui.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.david.lists.BuildConfig;
import com.example.david.lists.R;
import com.example.david.lists.data.datamodel.UserList;
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

public class MainActivity extends AppCompatActivity implements MyListFragment.ListFragmentListener {

    private ActivityMainBinding binding;
    private FragmentManager fragmentManager;

    private static final int RESPONSE_CODE_AUTH = 100;

    private boolean newActivity;

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
            addFragment(getUserListFragment());
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
        saveUserListDetails(
                widgetBundle.getString(getIntentKeyId(getApplicationContext())),
                widgetBundle.getString(getIntentKeyTitle(getApplicationContext()))
        );
        addFragment(getItemFragment());
    }

    private void saveUserListDetails(String id, String title) {
        SharedPreferences.Editor editor =
                getSharedPreferences(getString(R.string.key_shared_prefs_name), MODE_PRIVATE).edit();
        editor.putString(getString(R.string.key_shared_pref_user_list_id), id);
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


    private MyListFragment getUserListFragment() {
        return MyListFragment.newInstance(
                getString(R.string.displaying_user_list),
                true
        );
    }

    private MyListFragment getItemFragment() {
        return MyListFragment.newInstance(getString(R.string.displaying_item), false);
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
            case MyListFragment.ListFragmentListener.SIGN_OUT:
                signOut();
                break;
            case MyListFragment.ListFragmentListener.SIGN_IN:
                signIn();
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void openUserList(UserList userList) {
        saveUserListDetails(userList.getId(), userList.getTitle());
        addFragmentToBackStack(getItemFragment());
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
        Toast.makeText(this, stringResId, Toast.LENGTH_SHORT).show();
    }

    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
