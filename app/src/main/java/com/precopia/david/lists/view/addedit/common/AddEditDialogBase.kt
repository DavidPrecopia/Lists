package com.precopia.david.lists.view.addedit.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.precopia.david.lists.R
import com.precopia.david.lists.common.toast
import com.precopia.david.lists.util.UtilSoftKeyboard
import kotlinx.android.synthetic.main.add_edit_dialog.*
import javax.inject.Inject

abstract class AddEditDialogBase : DialogFragment(), IAddEditContract.View {

    @Inject
    lateinit var logic: IAddEditContract.Logic

    @Inject
    lateinit var utilSoftKeyboard: UtilSoftKeyboard

    protected abstract val currentTitle: String


    private val containerView by lazy {
        View.inflate(context, R.layout.add_edit_dialog, null) as ViewGroup
    }

    override fun getView() = containerView


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext()).setView(view).create()
        init()
        return dialog
    }

    override fun onStart() {
        super.onStart()
        utilSoftKeyboard.showKeyboardInDialog(text_input_edit_text)
    }

    private fun init() {
        initEditText()
        setHint()
        setConfirmButtonText()
        confirmClickListener()
        cancelClickListener()
        editTextListener()
    }

    private fun initEditText() {
        text_input_edit_text.apply {
            setText(currentTitle)
            setSelection(currentTitle.length)
        }
    }

    private fun setHint() {
        text_input_layout.hint = getString(R.string.hint_add_edit)
    }

    private fun setConfirmButtonText() {
        button_confirm.text = getString(R.string.button_text_save)
    }

    private fun confirmClickListener() {
        button_confirm.setOnClickListener { logic.validateInput(enteredText()) }
    }

    private fun cancelClickListener() {
        button_cancel.setOnClickListener { finishView() }
    }

    private fun editTextListener() {
        text_input_edit_text.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                logic.validateInput(enteredText())
                handled = true
            }
            handled
        }
    }

    private fun enteredText() =
            text_input_edit_text.text.toString().trim { it <= ' ' }


    override fun setStateError(message: String) {
        text_input_layout.error = message
    }

    override fun displayMessage(message: String) {
        toast(message)
    }


    override fun finishView() {
        dismiss()
    }


    override fun onStop() {
        utilSoftKeyboard.hideKeyboard()
        super.onStop()
    }
}