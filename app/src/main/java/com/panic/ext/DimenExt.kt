package com.panic.ext

import android.content.Context
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.Px
import androidx.fragment.app.Fragment

@Px
fun View.dimen(@DimenRes dimenRes: Int): Int {
    return resources.getDimensionPixelSize(dimenRes)
}

@Px
fun Context.dimen(@DimenRes dimenRes: Int): Int {
    return resources.getDimensionPixelSize(dimenRes)
}

@Px
fun Fragment.dimen(@DimenRes dimenRes: Int): Int {
    return resources.getDimensionPixelSize(dimenRes)
}