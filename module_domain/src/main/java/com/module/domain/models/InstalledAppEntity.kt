package com.module.domain.models

import android.graphics.drawable.Drawable

data class InstalledAppEntity(
    val packageName: String,
    val label: String,
    val icon: Drawable,
    val enabled: Boolean,
    val canConnect: Boolean,
)