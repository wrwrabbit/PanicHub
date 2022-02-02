package com.module.domain.usecases

import com.module.domain.models.AlarmEntity
import com.module.domain.repositories.DatabaseAlarmRepository

class AddAlarmUseCase constructor(
    private val databaseAlarmRepository: DatabaseAlarmRepository
) {
    suspend operator fun invoke(alarmEntity: AlarmEntity) {
        databaseAlarmRepository.insert(alarmEntity)
    }
}