package com.edwin.contacts.extensions

import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

inline fun Fragment.showSnackbar(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    f: Snackbar.() -> Unit = {
        action(android.R.string.ok) {
            this.dismiss()
        }
    }
) {
    val snack = Snackbar.make(requireView(), message, length)
    snack.f()
    snack.show()
}

fun Snackbar.action(@StringRes action: Int, color: Int? = null, listener: (View) -> Unit) {
    setAction(action, listener)
    color?.let { setActionTextColor(color) }
}