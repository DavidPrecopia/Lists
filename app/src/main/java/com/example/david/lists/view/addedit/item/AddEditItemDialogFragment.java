package com.example.david.lists.view.addedit.item;

import android.content.Context;
import android.os.Bundle;

import com.example.david.lists.view.addedit.common.AddEditDialogFragmentBase;
import com.example.david.lists.view.addedit.item.buildlogic.DaggerAddEditItemDialogFragmentComponent;

public final class AddEditItemDialogFragment extends AddEditDialogFragmentBase {

    private String id;
    private String currentTitle;
    private String userListId;

    private static final String ARG_KEY_ID = "arg_key_id";
    private static final String ARG_KEY_TITLE = "arg_key_title";
    private static final String ARG_KEY_USER_LIST_ID = "arg_key_user_list_id";

    public AddEditItemDialogFragment() {
    }

    public static AddEditItemDialogFragment getInstance(String id, String title, String userListId) {
        AddEditItemDialogFragment fragment = new AddEditItemDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_ID, id);
        bundle.putString(ARG_KEY_TITLE, title);
        bundle.putString(ARG_KEY_USER_LIST_ID, userListId);
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
        userListId = arguments.getString(ARG_KEY_USER_LIST_ID);
    }

    private void inject() {
        DaggerAddEditItemDialogFragmentComponent.builder()
                .application(getActivity().getApplication())
                .fragment(this)
                .id(id)
                .title(currentTitle)
                .userListId(userListId)
                .build()
                .inject(this);
    }


    @Override
    protected String getCurrentTitle() {
        return currentTitle;
    }
}