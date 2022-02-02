package com.module.domain.repositories

import com.module.domain.models.AlarmEntity
import kotlinx.coroutines.flow.Flow

interface DatabaseAlarmRepository {
    val alarmListFlow: Flow<List<AlarmEntity>?>
    suspend fun insert(alarm: AlarmEntity)
    suspend fun update(alarm: AlarmEntity)
    suspend fun delete(alarmId: String)
    suspend fun getItem(alarmId: String): AlarmEntity?
}