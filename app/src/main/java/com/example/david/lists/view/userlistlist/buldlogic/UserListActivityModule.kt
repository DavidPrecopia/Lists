package com.example.david.lists.view.userlistlist.buldlogic

import android.app.Application
import android.content.Intent
import com.example.david.lists.R
import com.example.david.lists.common.buildlogic.ViewScope
import com.example.david.lists.view.authentication.AuthView
import com.example.david.lists.view.authentication.IAuthContract
import dagger.Module
import dagger.Provides
import org.jetbrains.anko.intentFor

@Module
class UserListActivityModule {
    @ViewScope
    @Provides
    fun authIntent(application: Application): Intent {
        return application.intentFor<AuthView>(
                application.getString(R.string.intent_extra_auth) to IAuthContract.AuthGoal.SIGN_IN
        )
    }
}