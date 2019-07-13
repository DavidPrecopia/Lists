package com.example.david.lists.view.addedit.userlist;

import android.content.Context;
import android.os.Bundle;

import com.example.david.lists.view.addedit.common.AddEditDialogBase;
import com.example.david.lists.view.addedit.userlist.buildlogic.DaggerAddEditUserListDialogComponent;

public final class AddEditUserListDialog extends AddEditDialogBase {

    private static final String ARG_KEY_ID = "arg_key_id";
    private static final String ARG_KEY_TITLE = "arg_key_title";
    private static final String ARG_KEY_POSITION = "arg_key_position";

    public AddEditUserListDialog() {
    }

    public static AddEditUserListDialog getInstance(String id, String title, int position) {
        AddEditUserListDialog fragment = new AddEditUserListDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_ID, id);
        bundle.putString(ARG_KEY_TITLE, title);
        bundle.putInt(ARG_KEY_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        inject();
        super.onAttach(context);
    }

    private void inject() {
        DaggerAddEditUserListDialogComponent.builder()
                .application(getActivity().getApplication())
                .view(this)
                .id(getArguments().getString(ARG_KEY_ID))
                .title(getArguments().getString(ARG_KEY_TITLE))
                .position(getArguments().getInt(ARG_KEY_POSITION))
                .build()
                .inject(this);
    }

    @Override
    protected String getCurrentTitle() {
        return logic.getCurrentTitle();
    }
}