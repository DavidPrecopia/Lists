package com.precopia.david.lists.view.reauthentication.phone

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
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract.LogicEvents
import com.precopia.david.lists.view.reauthentication.phone.IPhoneReAuthContract.ViewEvents
import com.precopia.david.lists.view.reauthentication.phone.buildlogic.DaggerPhoneReAuthComponent
import javax.inject.Inject

class PhoneReAuthView : ReAuthBase(), IPhoneReAuthContract.View {

    @Inject
    lateinit var logic: IPhoneReAuthContract.Logic


    override val messageResId: Int
        get() = R.string.msg_phone_reauth

    override val inputType: Int
        get() = InputType.TYPE_CLASS_PHONE

    override val hintResId: Int
        get() = R.string.hint_phone_num

    override val buttonTextResId: Int
        get() = R.string.button_text_send_sms


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerPhoneReAuthComponent.builder()
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
            is ViewEvents.DisplayMessage -> displayMessage(event.message)
            is ViewEvents.DisplayError -> displayError(event.message)
            ViewEvents.DisplayLoading -> displayLoading()
            ViewEvents.HideLoading -> hideLoading()
            is ViewEvents.OpenSmsVerification ->
                openSmsVerification(event.phoneNum, event.verificationId)
            ViewEvents.FinishView -> finishView()
        }
    }

    override fun buttonClickListener(enteredText: String) {
        logic.onEvent(LogicEvents.ConfirmPhoneNumClicked(enteredText))
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


    private fun openSmsVerification(phoneNum: String, verificationId: String) {
        navigate(PhoneReAuthViewDirections.actionPhoneReAuthViewToSmsCodeView(
                phoneNum, verificationId
        ))
    }

    private fun finishView() {
        navigateUp()
    }
}
