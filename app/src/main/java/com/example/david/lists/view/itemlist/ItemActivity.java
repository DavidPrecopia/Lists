package com.example.david.lists.view.itemlist;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.example.david.lists.R;
import com.example.david.lists.databinding.ActivityItemBinding;
import com.example.david.lists.di.view.itemlist.DaggerItemActivityComponent;
import com.example.david.lists.view.common.ActivityBase;

public class ItemActivity extends ActivityBase {

    private ActivityItemBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        inject();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_item);
        init();
    }

    private void inject() {
        DaggerItemActivityComponent.builder()
                .application(getApplication())
                .activity(this)
                .build()
                .inject(this);
    }

    private void init() {
        if (newActivity) {
            addFragment(getItemFragment(), binding.fragmentHolder.getId());
        }
    }

    private Fragment getItemFragment() {
        return ItemFragment.newInstance(
                getStringExtra(R.string.intent_extra_user_list_id),
                getStringExtra(R.string.intent_extra_user_list_title)
        );
    }

    private String getStringExtra(int key) {
        return getIntent().getStringExtra(getString(key));
    }
}
