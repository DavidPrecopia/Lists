package com.example.david.lists.ui.addedit.userlist;

import android.content.Context;
import android.os.Bundle;

import com.example.david.lists.di.view.addeditfragment.userlist.DaggerAddEditUserListFragmentComponent;
import com.example.david.lists.ui.addedit.AddEditFragmentBase;

public final class AddEditUserListFragment extends AddEditFragmentBase {

    private String id;
    private String currentTitle;

    private static final String ARG_KEY_ID = "arg_key_id";
    private static final String ARG_KEY_TITLE = "arg_key_title";

    public AddEditUserListFragment() {
    }

    public static AddEditUserListFragment getInstance(String id, String title) {
        AddEditUserListFragment fragment = new AddEditUserListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_ID, id);
        bundle.putString(ARG_KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        initFields();
        inject();
        super.onAttach(context);
    }

    private void initFields() {
        Bundle arguments = getArguments();
        id = arguments.getString(ARG_KEY_ID);
        currentTitle = arguments.getString(ARG_KEY_TITLE);
    }

    private void inject() {
        DaggerAddEditUserListFragmentComponent.builder()
                .application(getActivity().getApplication())
                .fragment(this)
                .id(id)
                .title(currentTitle)
                .build()
                .inject(this);
    }

    @Override
    protected String getCurrentTitle() {
        return currentTitle;
    }
}