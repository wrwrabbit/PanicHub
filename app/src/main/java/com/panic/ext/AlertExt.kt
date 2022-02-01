package com.panic.ext

import android.app.Activity
import androidx.appcompat.app.AlertDialog

fun Activity.alertDialog(
    title: String? = null,
    message: String? = null
): AlertDialog {
    return AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(android.R.string.ok) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        .show()
}
