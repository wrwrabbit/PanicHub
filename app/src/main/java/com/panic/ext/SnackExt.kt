package com.panic.ext

import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.panic.R

fun View.snackbar(text: String) {
    Snackbar.make(this, text, Snackbar.LENGTH_LONG)
//        .setAction("Action", null)
        .show()
}