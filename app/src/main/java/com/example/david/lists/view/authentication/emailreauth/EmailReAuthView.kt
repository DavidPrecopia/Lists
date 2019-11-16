package com.example.david.lists.view.authentication.emailreauth

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.david.lists.R
import com.example.david.lists.common.application
import com.example.david.lists.common.toast
import com.example.david.lists.view.authentication.emailreauth.IEmailReAuthContract.ViewEvent
import com.example.david.lists.view.authentication.emailreauth.buildlogic.DaggerEmailReAuthComponent
import kotlinx.android.synthetic.main.email_re_auth_view.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

class EmailReAuthView : Fragment(R.layout.email_re_auth_view), IEmailReAuthContract.View {

    @Inject
    lateinit var logic: IEmailReAuthContract.Logic


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerEmailReAuthComponent.builder()
                .application(application)
                .view(this)
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initToolbar()
        initClickListener()
    }

    private fun initToolbar() {
        with(toolbar) {
            (activity as AppCompatActivity).setSupportActionBar(this)
            title = getString(R.string.title_delete_account)
            setNavigationIcon(R.drawable.ic_arrow_back_white_24dp)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun initClickListener() {
        button_confirm_delete.setOnClickListener {
            logic.onEvent(ViewEvent.DeleteAcctClicked(getEnteredPassword()))
        }
    }

    private fun getEnteredPassword() = text_input_edit_text.text.toString()


    override fun openAuthView() {
        findNavController().navigate(
                EmailReAuthViewDirections.actionEmailReAuthViewToAuthView()
        )
    }

    override fun finishView() {
        findNavController().navigateUp()
    }


    override fun displayMessage(message: String) {
        toast(message)
    }

    override fun displayError(message: String) {
        text_input_layout.error = message
    }
}
