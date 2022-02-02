package com.module.domain.usecases

import com.module.domain.models.AlarmEntity
import com.module.domain.repositories.DatabaseAlarmRepository
import kotlinx.coroutines.flow.first

class GetAlarmsUseCase constructor(
    private val databaseAlarmRepository: DatabaseAlarmRepository
) {
    suspend operator fun invoke(): List<AlarmEntity> {
        return databaseAlarmRepository.alarmListFlow.first()?: emptyList()
    }
}