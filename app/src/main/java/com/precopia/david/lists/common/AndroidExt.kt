package com.precopia.david.lists.common

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import org.jetbrains.anko.toast

val Fragment.application: Application
    get() = activity!!.application

fun Fragment.toast(message: String) = context!!.toast(message)

fun Fragment.navigate(direction: NavDirections) {
    findNavController().navigate(direction)
}

fun Fragment.navigateUp() {
    findNavController().navigateUp()
}


val String.onlyDigits
    get() = this.matches(Regex("[0-9]+"))