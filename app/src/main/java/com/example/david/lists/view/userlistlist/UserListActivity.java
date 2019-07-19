package com.example.david.lists.view.userlistlist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.example.david.lists.R;
import com.example.david.lists.data.repository.IRepositoryContract;
import com.example.david.lists.databinding.ActivityUserListBinding;
import com.example.david.lists.view.authentication.AuthView;
import com.example.david.lists.view.authentication.IAuthContract;
import com.example.david.lists.view.common.ActivityBase;
import com.example.david.lists.view.userlistlist.buldlogic.DaggerUserListActivityComponent;

import javax.inject.Inject;

public class UserListActivity extends ActivityBase
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private ActivityUserListBinding binding;

    @Inject
    SharedPreferences sharedPrefs;

    @Inject
    IRepositoryContract.UserRepository userRepo;

    private static final int RESPONSE_CODE = 100;

    private boolean newActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        inject();
        super.onCreate(savedInstanceState);
        this.newActivity = (savedInstanceState == null);
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

    private void init() {
        if (userRepo.signedOut()) {
            signIn();
        } else {
            initView();
        }
    }

    private void signIn() {
        startActivityForResult(
                getAuthIntent(),
                RESPONSE_CODE
        );
    }

    private Intent getAuthIntent() {
        Intent intent = new Intent(this, AuthView.class);
        intent.putExtra(getString(R.string.intent_extra_auth), IAuthContract.AuthGoal.SIGN_IN);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESPONSE_CODE) {
            evalAuthResult(data);
        }
    }

    private void evalAuthResult(Intent data) {
        if (authWasSuccessful(data)) {
            newActivity = true;
            initView();
        } else {
            finish();
        }
    }

    private boolean authWasSuccessful(Intent data) {
        return data.getSerializableExtra(getString(R.string.intent_extra_auth_result))
                == IAuthContract.AuthResult.AUTH_SUCCESS;
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
    protected void onResume() {
        super.onResume();
        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }


    /**
     * When the user signs-out, all Fragments need to be removed
     * to reset state.
     */
    @Override
    public void recreate() {
        if (userRepo.signedOut()) {
            removeAllFragments();
        }
        super.recreate();
    }
}
