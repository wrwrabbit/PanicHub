package com.module.domain

import android.graphics.drawable.Drawable

data class InstalledApp(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val enabled: Boolean,
    val canConnect: Boolean,
)