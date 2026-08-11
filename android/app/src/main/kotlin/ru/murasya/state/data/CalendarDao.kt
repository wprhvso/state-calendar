package ru.murasya.state.data

import androidx.room3.Dao
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarDao {
    @Query("SELECT * FROM day")
    fun marksFlow(): Flow<List<DayMark>>

    @Query("INSERT OR REPLACE INTO day (`date`, `state`) VALUES (:date, :state)")
    suspend fun put(date: Long, state: Int)

    @Query("DELETE FROM day WHERE `date` = :date")
    suspend fun clear(date: Long)

    @Query("SELECT `state` FROM brush WHERE `id` = 0")
    fun brushFlow(): Flow<List<Int>>

    @Query("INSERT OR REPLACE INTO brush (`id`, `state`) VALUES (0, :state)")
    suspend fun putBrush(state: Int)
}
