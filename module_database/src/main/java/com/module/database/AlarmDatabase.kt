package com.module.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.module.database.converters.DateConverter
import com.module.database.models.AlarmDB

@Database(entities = [AlarmDB::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object {
        const val DATABASE_NAME: String = "alarm_database"
    }
}
