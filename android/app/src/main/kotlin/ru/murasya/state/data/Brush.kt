package ru.murasya.state.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "brush")
data class Brush(
    @PrimaryKey
    val id: Int,
    val state: Int,
)
