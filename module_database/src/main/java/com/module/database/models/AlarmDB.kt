package com.module.database.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.module.database.converters.DateConverter
import java.util.Date

@Entity(tableName = AlarmDB.TABLE_NAME)
class AlarmDB constructor(
    @field:PrimaryKey
    var id: String,
    var packageName: String,
    var createdAt: Date
) {
    companion object {
        const val TABLE_NAME: String = "alarm_table"
    }
}

