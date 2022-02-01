package com.panic.handler.ext

import android.content.Context
import android.widget.Toast

fun Context.toastLong(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}