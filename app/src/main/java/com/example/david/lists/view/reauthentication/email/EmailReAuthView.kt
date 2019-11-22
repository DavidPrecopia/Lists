package com.example.david.lists.view.reauthentication.email

import android.content.Context
import android.text.InputType
import com.example.david.lists.R
import com.example.david.lists.common.application
import com.example.david.lists.common.navigate
import com.example.david.lists.common.navigateUp
import com.example.david.lists.common.toast
import com.example.david.lists.view.reauthentication.common.ReAuthBase
import com.example.david.lists.view.reauthentication.email.IEmailReAuthContract.ViewEvent
import com.example.david.lists.view.reauthentication.email.buildlogic.DaggerEmailReAuthComponent
import javax.inject.Inject

class EmailReAuthView : ReAuthBase(), IEmailReAuthContract.View {

    @Inject
    lateinit var logic: IEmailReAuthContract.Logic


    override val messageResId: Int
        get() = R.string.msg_email_reauth

    override val inputType: Int
        get() = InputType.TYPE_TEXT_VARIATION_PASSWORD

    override val hintResId: Int
        get() = R.string.hint_password

    override val buttonTextResId: Int
        get() = R.string.button_text_delete_account


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


    override fun buttonClickListener(enteredText: String) {
        logic.onEvent(ViewEvent.DeleteAcctClicked(enteredText))
    }


    override fun openAuthView() {
        navigate(EmailReAuthViewDirections.actionEmailReAuthViewToAuthView())
    }

    override fun finishView() {
        navigateUp()
    }


    override fun displayMessage(message: String) {
        toast(message)
    }

    override fun displayError(message: String) {
        displayErrorEditText(message)
    }


    override fun displayLoading() {
        displayProgressBar()
    }

    override fun hideLoading() {
        hideProgressBar()
    }
}
