package ru.murasya.state.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL

@Database(entities = [DayMark::class, Brush::class], version = 2, exportSchema = false)
abstract class CalendarDatabase : RoomDatabase() {
    abstract fun dao(): CalendarDao

    companion object {
        private const val NAME = "state.db"

        private val ADD_BRUSH =
            object : Migration(1, 2) {
                override suspend fun migrate(connection: SQLiteConnection) {
                    connection.execSQL(
                        "CREATE TABLE IF NOT EXISTS `brush` " +
                            "(`id` INTEGER NOT NULL, `state` INTEGER NOT NULL, PRIMARY KEY(`id`))",
                    )
                }
            }

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
                .addMigrations(ADD_BRUSH)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
