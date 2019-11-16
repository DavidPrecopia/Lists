package com.example.david.lists.common

import android.app.Application
import androidx.fragment.app.Fragment
import org.jetbrains.anko.toast

val Fragment.application: Application
    get() = activity!!.application

fun Fragment.toast(message: String) = context!!.toast(message)