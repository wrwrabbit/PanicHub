package com.module.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.module.database.models.AlarmDB
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @get:Query("SELECT * FROM alarm_table ORDER BY id ASC")
    val alarms: Flow<List<AlarmDB>?>

    @Query("SELECT * FROM alarm_table WHERE id=:alarmId ")
    suspend fun getItem(alarmId: String): AlarmDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alarm: AlarmDB)

    @Query("DELETE FROM alarm_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(alarm: AlarmDB)

    @Query("Delete from alarm_table where id = :alarmId")
    suspend fun delete(alarmId: String?)
}
