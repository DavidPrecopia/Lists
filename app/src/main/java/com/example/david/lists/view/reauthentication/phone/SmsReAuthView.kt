package com.example.david.lists.view.reauthentication.phone

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.view.View
import androidx.navigation.fragment.navArgs
import com.example.david.lists.R
import com.example.david.lists.common.application
import com.example.david.lists.common.navigate
import com.example.david.lists.common.navigateUp
import com.example.david.lists.common.toast
import com.example.david.lists.view.reauthentication.common.ReAuthBase
import com.example.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvent
import com.example.david.lists.view.reauthentication.phone.buildlogic.DaggerSmsReAuthViewComponent
import javax.inject.Inject

class SmsReAuthView : ReAuthBase(), ISmsReAuthContract.View {

    @Inject
    lateinit var logic: ISmsReAuthContract.Logic

    private lateinit var countDownTimer: CountDownTimer

    private val args: SmsReAuthViewArgs by navArgs()


    override val messageResId: Int
        get() = R.string.msg_sms_code

    override val inputType: Int
        get() = InputType.TYPE_CLASS_NUMBER

    override val hintResId: Int
        get() = R.string.hint_sms_code

    override val buttonTextResId: Int
        get() = R.string.button_text_delete_account


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    private fun inject() {
        DaggerSmsReAuthViewComponent.builder()
                .application(application)
                .view(this)
                .build()
                .inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logic.onEvent(ViewEvent.OnStart(args.phoneNum, args.verificationId))
    }


    override fun buttonClickListener(enteredText: String) {
        logic.onEvent(ViewEvent.ConfirmSmsClicked(enteredText))
    }


    override fun startTimer(durationSeconds: Long) {
        countDownTimer = object : CountDownTimer(durationSeconds * 1000, 1000) {
            override fun onFinish() {
                logic.onEvent(ViewEvent.TimerFinished)
            }

            override fun onTick(millisUntilFinished: Long) {
                // millisUntilFinished / 1000 == seconds
            }
        }.start()
    }

    override fun cancelTimer() {
        countDownTimer.cancel()
    }


    override fun displayMessage(message: String) {
        toast(message)
    }

    override fun displayError(message: String) {
        displayErrorEditText(message)
    }


    override fun openAuthView() {
        navigate(SmsReAuthViewDirections.actionSmsCodeViewToAuthView())
    }

    override fun finishView() {
        navigateUp()
    }
}