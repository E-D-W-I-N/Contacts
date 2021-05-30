package com.edwin.contacts.extensions

import android.content.DialogInterface
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.edwin.contacts.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun Fragment.makeDialog(
    @StringRes title: Int? = null,
    @StringRes message: Int? = null,
    positiveCallback: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() },
    negativeCallback: (dialogInterface: DialogInterface) -> Unit = { it.dismiss() },
    @StringRes positiveText: Int = android.R.string.ok,
    @StringRes negativeText: Int = android.R.string.cancel

): MaterialAlertDialogBuilder {
    return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogStyle)
        .apply {
            title?.let { setTitle(it) }
            message?.let { setMessage(it) }
            setPositiveButton(positiveText) { dialogInterface, _ -> positiveCallback(dialogInterface) }
            setNegativeButton(negativeText) { dialogInterface, _ -> negativeCallback(dialogInterface) }
        }
}
