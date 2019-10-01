package com.example.david.lists.view.itemlist

import android.os.Bundle
import com.example.david.lists.R
import com.example.david.lists.view.common.ActivityBase
import com.example.david.lists.view.itemlist.buldlogic.DaggerItemActivityComponent
import kotlinx.android.synthetic.main.activity_item.*

class ItemActivity : ActivityBase() {

    private var newActivity: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        inject()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        this.newActivity = savedInstanceState === null
        init()
    }

    private fun inject() {
        DaggerItemActivityComponent.builder()
                .application(application)
                .activity(this)
                .build()
                .inject(this)
    }

    private fun init() {
        if (newActivity) {
            addFragment(getItemFragment(), fragment_holder.id)
        }
    }

    private fun getItemFragment() = ItemListView.newInstance(
            getStringExtra(R.string.intent_extra_user_list_id),
            getStringExtra(R.string.intent_extra_user_list_title)
    )

    private fun getStringExtra(key: Int) =
            intent.getStringExtra(getString(key))
}
