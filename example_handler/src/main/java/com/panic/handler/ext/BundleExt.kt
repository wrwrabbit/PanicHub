package com.panic.handler.ext

import android.os.Bundle
import android.util.Log

fun Bundle.printData(tag: String) {
    if (keySet().isEmpty()){
        Log.e(tag, "Bundle data: isEmpty")
    }else {
        for (key in keySet()) {
            Log.e(tag, "Bundle data: " + key + " : " + get(key))
        }
    }
}
