package com.example.david.lists.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.DialogFragmentAddBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

public final class AddDialogFragment extends DialogFragment {

    private DialogFragmentAddBinding binding;

    private String hintText;
    private static final String ARG_KEY_HINT_TEXT = "hint_text_key";

    private AddDialogFragmentListener dialogListener;


    public AddDialogFragment() {
    }

    static AddDialogFragment getInstance(String hintText) {
        AddDialogFragment dialogFragment = new AddDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_KEY_HINT_TEXT, hintText);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dialogListener = (AddDialogFragmentListener) getTargetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_add, container, false);
        init();
        return binding.getRoot();
    }

    private void init() {
        setHint();
        confirmClickListener();
        cancelClickListener();
    }

    private void setHint() {
        binding.textInputLayout.setHint(getArguments().getString(ARG_KEY_HINT_TEXT));
    }

    private void confirmClickListener() {
        binding.buttonConfirm.setOnClickListener(view -> {
            String name = binding.textInputEditText.getText().toString();
            if (invalidInput(name)) {
                showError();
            } else {
                dialogListener.add(name);
                dismiss();
            }
        });
    }

    private void cancelClickListener() {
        binding.buttonCancel.setOnClickListener(view -> dismiss());
    }


    private void showError() {
        binding.textInputLayout.setError(getString(R.string.notice_empty_name_text_field));
    }

    private boolean invalidInput(String msg) {
        return TextUtils.isEmpty(msg);
    }


    interface AddDialogFragmentListener {
        void add(String name);
    }
}