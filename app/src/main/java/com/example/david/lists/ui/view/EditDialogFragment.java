package com.example.david.lists.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.example.david.lists.R;
import com.example.david.lists.databinding.DialogFragmentSharedBinding;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

public final class EditDialogFragment extends DialogFragment {

    private DialogFragmentSharedBinding binding;

    private EditingInfo editingInfo;
    private static final String ARG_KEY_EDITING_INFO = "edited_key";

    private EditDialogFragmentListener dialogListener;

    public EditDialogFragment() {
    }


    static EditDialogFragment getInstance(EditingInfo editingInfo) {
        EditDialogFragment fragment = new EditDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_KEY_EDITING_INFO, editingInfo);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editingInfo = getArguments().getParcelable(ARG_KEY_EDITING_INFO);
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
        setStyle(STYLE_NO_TITLE, R.style.DialogStyle);
        getDialog().setTitle("sdfsdf");
        init();
        return binding.getRoot();
    }

    private void init() {
        setEditText();
        setConfirmButtonText();
        confirmClickListener();
        cancelClickListener();
        editTextListener();
        UIUtil.showKeyboardInDialog(getDialog(), binding.textInputEditText);
    }

    private void setEditText() {
        binding.textInputEditText.setText(editingInfo.getTitle());
    }

    private void setConfirmButtonText() {
        binding.buttonConfirm.setText(getString(R.string.button_text_confirm_edit));
    }

    private void confirmClickListener() {
        binding.buttonConfirm.setOnClickListener(view -> processInput());
    }

    private void cancelClickListener() {
        binding.buttonCancel.setOnClickListener(view -> dismiss());
    }

    private void editTextListener() {
        binding.textInputEditText.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                processInput();
                handled = true;
            }
            return handled;
        });
    }


    private void processInput() {
        String newTitle = binding.textInputEditText.getText().toString();
        if (emptyInput(newTitle)) {
            showError(getString(R.string.error_empty_title_text_field));
        } else if (titleUnchanged(newTitle)) {
            showError(getString(R.string.error_title_unchanged));
        } else {
            dialogListener.edit(editingInfo.getId(), newTitle);
            dismiss();
        }
    }


    private void showError(String message) {
        binding.textInputLayout.setError(message);
    }

    private boolean emptyInput(String msg) {
        return TextUtils.isEmpty(msg);
    }

    private boolean titleUnchanged(String newTitle) {
        return newTitle.equals(editingInfo.getTitle());
    }


    @Override
    public void dismiss() {
        UIUtil.hideKeyboard(getContext(), binding.getRoot());
        super.dismiss();
    }


    public interface EditDialogFragmentListener {
        void edit(int id, String newTitle);
    }
}