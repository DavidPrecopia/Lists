package com.example.david.lists.view.reauthentication.phone

import android.content.Context
import android.text.InputType
import com.example.david.lists.R
import com.example.david.lists.common.application
import com.example.david.lists.common.navigate
import com.example.david.lists.common.navigateUp
import com.example.david.lists.common.toast
import com.example.david.lists.view.reauthentication.common.ReAuthBase
import com.example.david.lists.view.reauthentication.phone.IPhoneReAuthContract.ViewEvent
import com.example.david.lists.view.reauthentication.phone.buildlogic.DaggerPhoneReAuthViewComponent
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
        DaggerPhoneReAuthViewComponent.builder()
                .application(application)
                .view(this)
                .build()
                .inject(this)
    }


    override fun buttonClickListener(enteredText: String) {
        logic.onEvent(ViewEvent.ConfirmPhoneNumClicked(enteredText))
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


    override fun openSmsVerification(phoneNum: String, verificationId: String) {
        navigate(PhoneReAuthViewDirections.actionPhoneReAuthViewToSmsCodeView(
                phoneNum, verificationId
        ))
    }

    override fun finishView() {
        navigateUp()
    }
}
