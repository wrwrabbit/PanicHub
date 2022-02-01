package com.panic.handler.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.panic.handler.R
import com.panic.handler.ext.toastLong

class PanicBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        context?.toastLong(context.getString(R.string.app_name) +" PanicBroadcast.onReceive")
    }
}