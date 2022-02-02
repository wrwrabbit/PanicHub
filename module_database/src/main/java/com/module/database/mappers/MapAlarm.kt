package com.module.database.mappers

import com.module.database.models.AlarmDB
import com.module.domain.models.AlarmEntity

 fun AlarmEntity.toAlarmDB(): AlarmDB {
    return AlarmDB(
        id = id,
        packageName = packageName,
        createdAt = createdAt
    )
}

 fun AlarmDB.toAlarmEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        packageName = packageName,
        createdAt = createdAt
    )
}
