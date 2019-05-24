package com.example.david.lists.ui.addedit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.david.lists.R;
import com.example.david.lists.databinding.AddEditDialogFragmentBinding;
import com.example.david.lists.util.UtilSoftKeyboard;

public abstract class AddEditFragmentBase extends DialogFragment {

    private AddEditViewModelBase viewModel;

    private AddEditDialogFragmentBinding binding;
    private UtilSoftKeyboard utilSoftKeyboard;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.binding = DataBindingUtil.inflate(inflater, R.layout.add_edit_dialog_fragment, container, false);
        this.viewModel = getViewModel();
        init();
        return binding.getRoot();
    }


    protected abstract String getCurrentTitle();

    protected abstract AddEditViewModelBase getViewModel();


    private void init() {
        setEditText();
        setHint();
        setConfirmButtonText();
        confirmClickListener();
        cancelClickListener();
        editTextListener();
        initSoftKeyboardUtil();
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
        binding.buttonConfirm.setText(getString(R.string.button_text_confirm_add));
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

    private void initSoftKeyboardUtil() {
        utilSoftKeyboard = new UtilSoftKeyboard();
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