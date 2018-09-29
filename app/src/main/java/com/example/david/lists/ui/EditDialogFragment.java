package com.example.david.lists.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.DialogFragmentSharedBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

public final class EditDialogFragment extends DialogFragment {

    private DialogFragmentSharedBinding binding;

    private static final String ARG_KEY_ID = "id_key";
    private static final String ARG_KEY_TITLE = "title_key";

    private EditDialogFragmentListener dialogListener;

    public EditDialogFragment() {
    }


    static EditDialogFragment getInstance(int id, String title) {
        EditDialogFragment fragment = new EditDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_KEY_ID, id);
        bundle.putString(ARG_KEY_TITLE, title);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogListener = (EditDialogFragmentListener) getTargetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_shared, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        setEditText();
        setConfirmButtonText();
        confirmClickListener();
        cancelClickListener();
    }

    private void setEditText() {
        binding.textInputEditText.setText(getArguments().getString(ARG_KEY_TITLE));
    }

    private void setConfirmButtonText() {
        binding.buttonConfirm.setText(getString(R.string.button_text_confirm_edit));
    }

    private void confirmClickListener() {
        binding.buttonConfirm.setOnClickListener(view -> {
            String newTitle = binding.textInputEditText.getText().toString();
            if (invalidInput(newTitle)) {
                showError(getString(R.string.error_empty_title_text_field));
            } else if (titleUnchanged(newTitle)) {
                showError(getString(R.string.error_title_unchanged));
            } else {
                dialogListener.edit(getArguments().getInt(ARG_KEY_ID), newTitle);
                dismiss();
            }
        });
    }

    private void cancelClickListener() {
        binding.buttonCancel.setOnClickListener(view -> dismiss());
    }


    private void showError(String message) {
        binding.textInputLayout.setError(message);
    }

    private boolean invalidInput(String msg) {
        return TextUtils.isEmpty(msg);
    }

    private boolean titleUnchanged(String newTitle) {
        return newTitle.equals(getArguments().getString(ARG_KEY_TITLE));
    }


    interface EditDialogFragmentListener {
        void edit(int id, String newTitle);
    }
}
