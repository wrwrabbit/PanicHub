package com.module.database

import com.module.database.mappers.toAlarmDB
import com.module.database.mappers.toAlarmEntity
import com.module.domain.models.AlarmEntity
import com.module.domain.repositories.DatabaseAlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseAlarmRepositoryImpl constructor(alarmDatabase: AlarmDatabase) : DatabaseAlarmRepository {

    private val alarmDao: AlarmDao = alarmDatabase.alarmDao()

    override val alarmListFlow: Flow<List<AlarmEntity>?> = alarmDao.alarms
        .map { list -> list?.map { it.toAlarmEntity() } }

    override suspend fun insert(alarm: AlarmEntity) {
        alarmDao.insert(alarm.toAlarmDB())
    }

    override suspend fun update(alarm: AlarmEntity) {
        alarmDao.update(alarm.toAlarmDB())
    }

    override suspend fun delete(alarmId: String) {
        alarmDao.delete(alarmId)
    }

    override suspend fun getItem(alarmId: String): AlarmEntity? {
        val item = alarmDao.getItem(alarmId)
        return item?.toAlarmEntity()
    }
}
