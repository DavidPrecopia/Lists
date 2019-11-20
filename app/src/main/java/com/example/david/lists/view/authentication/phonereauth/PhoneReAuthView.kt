package com.example.david.lists.view.authentication.phonereauth

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import com.example.david.lists.R
import com.example.david.lists.common.*
import com.example.david.lists.view.authentication.phonereauth.IPhoneReAuthContract.ViewEvent

class PhoneReAuthView : Fragment(R.layout.phone_re_auth_view), IPhoneReAuthContract.View {

    // TODO @Inject
    lateinit var logic: PhoneReAuthLogic

    private lateinit var countDownTimer: CountDownTimer


    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    // TODO Update to Dagger
    private fun inject() {
        logic = PhoneReAuthLogic(
                this,
                PhoneReAuthViewModel(application),
                (application as ListsApplication).appComponent.userRepo()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logic.onEvent(ViewEvent.ConfirmPhoneNumClicked("+14127011790"))
    }


    override fun displaySmsVerification() {
        TODO("Not implemented")
    }

    override fun startTimer(durationSeconds: Long) {
        countDownTimer = object : CountDownTimer(durationSeconds * 1000, 1000) {
            override fun onFinish() {
                logic.onEvent(ViewEvent.TimerFinished)
            }

            override fun onTick(millisUntilFinished: Long) {
                // TODO Update the UI on each tick
                // millisUntilFinished / 1000
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
        TODO("not implemented")
    }


    override fun openAuthView() {
        navigate(PhoneReAuthViewDirections.actionPhoneReAuthViewToAuthView())
    }

    override fun finishView() {
        navigateUp()
    }
}
