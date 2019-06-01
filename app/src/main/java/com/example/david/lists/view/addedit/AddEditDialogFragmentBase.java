package com.example.david.lists.view.addedit;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.david.lists.R;
import com.example.david.lists.databinding.AddEditDialogFragmentBinding;
import com.example.david.lists.util.UtilSoftKeyboard;

import javax.inject.Inject;

public abstract class AddEditDialogFragmentBase extends DialogFragment {

    private AddEditDialogFragmentBinding binding;

    @Inject
    AddEditViewModelBase viewModel;

    @Inject
    UtilSoftKeyboard utilSoftKeyboard;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.add_edit_dialog_fragment, null, false);
        this.binding = DataBindingUtil.bind(view);
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        init();
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initSoftKeyboardUtil();
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    protected abstract String getCurrentTitle();


    private void init() {
        setEditText();
        setHint();
        setConfirmButtonText();
        confirmClickListener();
        cancelClickListener();
        editTextListener();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getEventErrorMessage().observe(this, this::showError);
        viewModel.getEventDismiss().observe(this, aVoid -> dismiss());
    }

    private void setEditText() {
        binding.textInputEditText.setText(getCurrentTitle());
    }

    private void setHint() {
        binding.textInputLayout.setHint(getString(R.string.hint_add_edit));
    }

    private void setConfirmButtonText() {
        binding.buttonConfirm.setText(getString(R.string.button_text_save));
    }

    private void confirmClickListener() {
        binding.buttonConfirm.setOnClickListener(view -> viewModel.validateInput(getEnteredText()));
    }

    private void cancelClickListener() {
        binding.buttonCancel.setOnClickListener(view -> dismiss());
    }

    private void editTextListener() {
        binding.textInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.validateInput(getEnteredText());
                handled = true;
            }
            return handled;
        });
    }


    /**
     * Needs to be called from `onCreateView()` otherwise `getDialog()` returns null.
     */
    private void initSoftKeyboardUtil() {
        utilSoftKeyboard.showKeyboardInDialog(getDialog(), binding.textInputEditText);
    }


    private String getEnteredText() {
        return binding.textInputEditText.getText().toString().trim();
    }

    private void showError(String errorMsg) {
        binding.textInputLayout.setError(errorMsg);
    }


    @Override
    public void dismiss() {
        utilSoftKeyboard.hideKeyboard(getContext(), binding.getRoot());
        super.dismiss();
    }
}