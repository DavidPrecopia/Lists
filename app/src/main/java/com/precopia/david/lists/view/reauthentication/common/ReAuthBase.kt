package com.precopia.david.lists.view.reauthentication.common

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.precopia.david.lists.R
import com.precopia.david.lists.common.navigateUp
import kotlinx.android.synthetic.main.reauth_view.*
import kotlinx.android.synthetic.main.toolbar.*

abstract class ReAuthBase : Fragment(R.layout.reauth_view) {

    abstract val messageResId: Int

    abstract val inputType: Int

    abstract val hintResId: Int

    abstract val buttonTextResId: Int


    abstract fun buttonClickListener(enteredText: String)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initText()
        initToolbar()
        initClickListener()
    }

    private fun initText() {
        tv_message.text = getString(messageResId)
        text_input_edit_text.inputType = inputType
        text_input_layout.hint = getString(hintResId)
        button_delete_account.text = getString(buttonTextResId)
    }

    private fun initToolbar() {
        with(toolbar) {
            (activity as AppCompatActivity).setSupportActionBar(this)
            title = getString(R.string.title_delete_account)
            setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
            setNavigationOnClickListener { navigateUp() }
        }
    }

    private fun initClickListener() {
        button_delete_account.setOnClickListener {
            buttonClickListener(getEnteredText())
        }
    }

    private fun getEnteredText() = text_input_edit_text.text.toString()


    protected fun displayProgressBar() {
        progress_bar.visibility = View.VISIBLE
        root_layout.visibility = View.GONE
    }

    protected fun hideProgressBar() {
        progress_bar.visibility = View.GONE
        root_layout.visibility = View.VISIBLE
    }


    protected fun displayErrorEditText(message: String) {
        text_input_layout.error = message
    }
}