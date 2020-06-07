package com.precopia.david.lists.view.addedit.common

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.precopia.david.lists.R
import com.precopia.david.lists.common.toast
import com.precopia.david.lists.util.UtilSoftKeyboard
import com.precopia.david.lists.view.addedit.common.IAddEditContract.LogicEvents
import com.precopia.david.lists.view.addedit.common.IAddEditContract.ViewEvents
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
        logic.observe().observe(this, Observer { evalViewEvents(it) })
        return dialog
    }

    override fun onStart() {
        super.onStart()
        utilSoftKeyboard.showKeyboardInDialog(text_input_edit_text)
    }


    private fun evalViewEvents(event: ViewEvents) {
        when (event) {
            is ViewEvents.SetStateError -> setStateError(event.message)
            is ViewEvents.DisplayMessage -> displayMessage(event.message)
            ViewEvents.FinishView -> finishView()
        }
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
        button_confirm.setOnClickListener { logic.onEvent(LogicEvents.Save(enteredText())) }
    }

    private fun cancelClickListener() {
        button_cancel.setOnClickListener { finishView() }
    }

    private fun editTextListener() {
        text_input_edit_text.setOnEditorActionListener { _, actionId, _ ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                logic.onEvent(LogicEvents.Save(enteredText()))
                handled = true
            }
            handled
        }
    }

    private fun enteredText() =
            text_input_edit_text.text.toString().trim { it <= ' ' }


    private fun setStateError(message: String) {
        text_input_layout.error = message
    }

    private fun displayMessage(message: String) {
        toast(message)
    }


    private fun finishView() {
        dismiss()
    }


    override fun onStop() {
        utilSoftKeyboard.hideKeyboard()
        super.onStop()
    }
}