package com.example.david.lists.view.userlistlist

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import com.example.david.lists.R
import com.example.david.lists.view.authentication.IAuthContract
import com.example.david.lists.view.common.ActivityBase
import com.example.david.lists.view.userlistlist.buldlogic.DaggerUserListActivityComponent
import kotlinx.android.synthetic.main.activity_user_list.*
import javax.inject.Inject

class UserListActivity : ActivityBase(), SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private var newActivity = false


    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        this.newActivity = savedInstanceState === null
        initView()
    }

    private fun inject() {
        DaggerUserListActivityComponent.builder()
                .application(application)
                .activity(this)
                .build()
                .inject(this)
    }


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


    override fun onBackPressed() {
        setResult(IAuthContract.FINISH)
        super.onBackPressed()
    }
}
