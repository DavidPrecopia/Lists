package com.example.david.lists.view.reauthentication.phone

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.david.lists.R
import com.example.david.lists.common.application
import com.example.david.lists.common.navigate
import com.example.david.lists.common.navigateUp
import com.example.david.lists.common.toast
import com.example.david.lists.view.reauthentication.phone.ISmsReAuthContract.ViewEvent
import com.example.david.lists.view.reauthentication.phone.buildlogic.DaggerSmsReAuthViewComponent
import kotlinx.android.synthetic.main.sms_reauth_view.*
import kotlinx.android.synthetic.main.toolbar.*
import javax.inject.Inject

private const val TIME_LEFT_KEY = "time_left_key"

class SmsReAuthView : Fragment(R.layout.sms_reauth_view), ISmsReAuthContract.View {

    @Inject
    lateinit var logic: ISmsReAuthContract.Logic

    @Inject
    lateinit var sharedPrefs: SharedPreferences

    private lateinit var countDownTimer: CountDownTimer

    private var timeLeft: Long = 0

    private val args: SmsReAuthViewArgs by navArgs()

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
        initView()
        logic.onEvent(ViewEvent.OnStart(
                args.phoneNum,
                args.verificationId,
                sharedPrefs.getLong(TIME_LEFT_KEY, -1L)
        ))
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
            setNavigationOnClickListener { navigateUp() }
        }
    }

    private fun initClickListener() {
        button_delete_account.setOnClickListener {
            buttonClickListener(getEnteredText())
        }
    }

    private fun getEnteredText() = text_input_edit_text.text.toString()


    private fun buttonClickListener(enteredText: String) {
        logic.onEvent(ViewEvent.ConfirmSmsClicked(enteredText))
    }


    override fun startTimer(durationSeconds: Long) {
        countDownTimer = object : CountDownTimer(durationSeconds * 1000, 1000) {
            override fun onFinish() {
                logic.onEvent(ViewEvent.TimerFinished)
            }

            override fun onTick(millisUntilFinished: Long) {
                timeLeft = { millisUntilFinished / 1000 }.invoke()
                tv_timer.text = getString(
                        R.string.msg_sms_code_timer_left_arg,
                        timeLeft.toString()
                )
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


    override fun displayLoading() {
        displayProgressBar()
    }

    override fun hideLoading() {
        hideProgressBar()
    }


    private fun displayProgressBar() {
        progress_bar.visibility = View.VISIBLE
        root_layout.visibility = View.GONE
    }

    private fun hideProgressBar() {
        progress_bar.visibility = View.GONE
        root_layout.visibility = View.VISIBLE
    }

    private fun displayErrorEditText(message: String) {
        text_input_layout.error = message
    }


    override fun openAuthView() {
        navigate(SmsReAuthViewDirections.actionSmsCodeViewToAuthView())
    }

    override fun finishView() {
        navigateUp()
    }


    override fun onDestroyView() {
        sharedPrefs.edit { putLong(TIME_LEFT_KEY, timeLeft) }
        logic.onEvent(ViewEvent.ViewDestroyed)
        super.onDestroyView()
    }
}