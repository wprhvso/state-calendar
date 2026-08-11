package ru.murasya.state.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

@Database(entities = [DayMark::class], version = 1, exportSchema = false)
abstract class CalendarDatabase : RoomDatabase() {
    abstract fun dao(): CalendarDao

    companion object {
        private const val NAME = "state.db"

        @Volatile
        private var instance: CalendarDatabase? = null

        fun get(context: Context): CalendarDatabase =
            instance ?: synchronized(this) {
                instance ?: build(context.applicationContext).also { instance = it }
            }

        private fun build(context: Context): CalendarDatabase =
            Room
                .databaseBuilder<CalendarDatabase>(context, NAME)
                .setDriver(AndroidSQLiteDriver())
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
