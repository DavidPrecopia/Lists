package com.example.david.lists.view.addedit.item;

import android.content.Context;
import android.os.Bundle;

import com.example.david.lists.view.addedit.common.AddEditDialogBase;
import com.example.david.lists.view.addedit.item.buildlogic.DaggerAddEditItemDialogComponent;

public final class AddEditItemDialog extends AddEditDialogBase {

    private static final String ARG_KEY_ID = "arg_key_id";
    private static final String ARG_KEY_TITLE = "arg_key_title";
    private static final String ARG_KEY_USER_LIST_ID = "arg_key_user_list_id";
    private static final String ARG_KEY_POSITION = "arg_key_position";

    public AddEditItemDialog() {
    }

    public static AddEditItemDialog getInstance(String id, String title, String userListId, int position) {
        AddEditItemDialog fragment = new AddEditItemDialog();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_ID, id);
        bundle.putString(ARG_KEY_TITLE, title);
        bundle.putString(ARG_KEY_USER_LIST_ID, userListId);
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
        DaggerAddEditItemDialogComponent.builder()
                .application(getActivity().getApplication())
                .view(this)
                .id(getArguments().getString(ARG_KEY_ID))
                .title(getArguments().getString(ARG_KEY_TITLE))
                .userListId(getArguments().getString(ARG_KEY_USER_LIST_ID))
                .position(getArguments().getInt(ARG_KEY_POSITION))
                .build()
                .inject(this);
    }


    @Override
    protected String getCurrentTitle() {
        return logic.getCurrentTitle();
    }
}