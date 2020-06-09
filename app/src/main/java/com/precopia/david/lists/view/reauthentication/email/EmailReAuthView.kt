package com.precopia.david.lists.view.reauthentication.email

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.lifecycle.Observer
import com.precopia.david.lists.R
import com.precopia.david.lists.common.application
import com.precopia.david.lists.common.navigate
import com.precopia.david.lists.common.navigateUp
import com.precopia.david.lists.common.toast
import com.precopia.david.lists.view.reauthentication.common.ReAuthBase
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.email.IEmailReAuthContract.ViewEvents
import com.precopia.david.lists.view.reauthentication.email.buildlogic.DaggerEmailReAuthComponent
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logic.observe().observe(viewLifecycleOwner, Observer { evalViewEvents(it) })
    }

    private fun evalViewEvents(event: ViewEvents) {
        when (event) {
            ViewEvents.OpenAuthView -> openAuthView()
            is ViewEvents.DisplayMessage -> displayMessage(event.message)
            is ViewEvents.DisplayError -> displayError(event.message)
            ViewEvents.DisplayLoading -> displayLoading()
            ViewEvents.HideLoading -> hideLoading()
            ViewEvents.FinishView -> finishView()
        }
    }


    override fun buttonClickListener(enteredText: String) {
        logic.onEvent(LogicEvents.DeleteAcctClicked(enteredText))
    }


    private fun openAuthView() {
        navigate(EmailReAuthViewDirections.actionEmailReAuthViewToAuthView())
    }

    private fun finishView() {
        navigateUp()
    }


    private fun displayMessage(message: String) {
        toast(message)
    }

    private fun displayError(message: String) {
        displayErrorEditText(message)
    }


    private fun displayLoading() {
        displayProgressBar()
    }

    private fun hideLoading() {
        hideProgressBar()
    }
}
