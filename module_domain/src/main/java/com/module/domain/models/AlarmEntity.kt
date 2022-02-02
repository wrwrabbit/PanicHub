package com.module.domain.models

import java.util.Date

data class AlarmEntity(
    val id: String,
    val packageName: String,
    val createdAt: Date,
)