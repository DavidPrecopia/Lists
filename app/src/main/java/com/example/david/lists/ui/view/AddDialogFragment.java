package com.example.david.lists.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.david.lists.R;
import com.example.david.lists.databinding.DialogFragmentSharedBinding;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

public final class AddDialogFragment extends DialogFragment {

    private DialogFragmentSharedBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_fragment_shared, container, false);
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle);
        init();
        return binding.getRoot();
    }

    private void init() {
        setHint();
        setConfirmButtonText();
        confirmClickListener();
        cancelClickListener();
        UIUtil.showKeyboardInDialog(getDialog(), binding.textInputEditText);
    }

    private void setHint() {
        binding.textInputLayout.setHint(getArguments().getString(ARG_KEY_HINT_TEXT));
    }

    private void setConfirmButtonText() {
        binding.buttonConfirm.setText(getString(R.string.button_text_confirm_add));
    }

    private void confirmClickListener() {
        binding.buttonConfirm.setOnClickListener(view -> {
            String title = binding.textInputEditText.getText().toString();
            if (emptyInput(title)) {
                showError();
            } else {
                dialogListener.add(title);
                dismiss();
            }
        });
    }

    private void cancelClickListener() {
        binding.buttonCancel.setOnClickListener(view -> dismiss());
    }


    @Override
    public void dismiss() {
        UIUtil.hideKeyboard(getContext(), binding.getRoot());
        super.dismiss();
    }


    private void showError() {
        binding.textInputLayout.setError(getString(R.string.error_empty_title_text_field));
    }

    private boolean emptyInput(String msg) {
        return TextUtils.isEmpty(msg);
    }


    public interface AddDialogFragmentListener {
        void add(String title);
    }
}