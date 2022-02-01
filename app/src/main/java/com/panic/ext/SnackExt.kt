package com.panic.ext

import android.view.View
import com.google.android.material.snackbar.Snackbar

fun View.snackbar(text: String) {
    Snackbar.make(this, text, Snackbar.LENGTH_LONG)
//        .setAction("Action", null)
        .show()
}