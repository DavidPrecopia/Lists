package com.example.david.lists.view.userlistlist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.example.david.lists.R
import com.example.david.lists.data.repository.IRepositoryContract
import com.example.david.lists.view.authentication.IAuthContract
import com.example.david.lists.view.common.ActivityBase
import com.example.david.lists.view.userlistlist.buldlogic.DaggerUserListActivityComponent
import kotlinx.android.synthetic.main.activity_user_list.*
import javax.inject.Inject
import javax.inject.Provider

/**
 * This does not have a Logic class because I (arbitrarily) feel
 * it is overkill.
 */
class UserListActivity : ActivityBase(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        private const val RESPONSE_CODE = 100
    }

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    @Inject
    lateinit var userRepo: IRepositoryContract.UserRepository

    @Inject
    lateinit var authIntent: Provider<Intent>

    private var newActivity: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        this.newActivity = savedInstanceState === null
        init()
    }

    private fun inject() {
        DaggerUserListActivityComponent.builder()
                .application(application)
                .activity(this)
                .build()
                .inject(this)
    }

    private fun init() {
        if (userRepo.signedOut) {
            signIn()
        } else {
            initView()
        }
    }


    private fun signIn() {
        startActivityForResult(
                authIntent.get(),
                RESPONSE_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESPONSE_CODE) {
            evalAuthResult(data)
        }
    }

    private fun evalAuthResult(data: Intent?) {
        if (authWasSuccessful(data!!)) {
            newActivity = true
            initView()
        } else {
            finish()
        }
    }

    private fun authWasSuccessful(data: Intent) =
            data.getSerializableExtra(getString(R.string.intent_extra_auth_result)) ===
                    IAuthContract.AuthResult.AUTH_SUCCESS


    private fun initView() {
        progress_bar.visibility = View.GONE
        if (newActivity) {
            addFragment(UserListListView.newInstance(), fragment_holder.id)
        }
    }


    override fun onSharedPreferenceChanged(sharedPrefs: SharedPreferences, key: String) {
        if (key == getString(R.string.night_mode_shared_pref_key)) {
            recreate()
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPrefs.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
    }


    /**
     * When the user signs-out, all Fragments need to be removed
     * to reset state.
     */
    override fun recreate() {
        if (userRepo.signedOut) {
            removeAllFragments()
        }
        super.recreate()
    }
}
