package ru.murasya.state.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "day")
data class DayMark(
    @PrimaryKey
    val date: Long,
    val state: Int,
)
