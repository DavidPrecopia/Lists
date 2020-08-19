package com.precopia.david.lists.view.preferences.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.precopia.david.lists.R
import com.precopia.david.lists.util.IUtilThemeContract.ThemeLabels

class ThemeDialog : DialogFragment() {


    interface ThemeChangeListener : Parcelable {
        fun themeChanged(label: ThemeLabels, selectedIndex: Int)
    }

    private val args: ThemeDialogArgs by navArgs()

    private val themeLabelsList = listOf(
            ThemeLabels.DAY,
            ThemeLabels.DARK,
            ThemeLabels.FOLLOW_SYSTEM
    )


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.prefs_title_theme))
                .setSingleChoiceItems(getLabels(), args.selectedIndex) { dialog, position ->
                    dialog.dismiss()
                    args.listener.themeChanged(themeLabelsList[position], position)
                }
                .create()
    }

    private fun getLabels() = themeLabelsList.map { it.label }.toTypedArray()
}