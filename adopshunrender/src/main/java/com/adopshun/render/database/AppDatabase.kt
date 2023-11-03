package com.adopshun.render.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Step::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stepDao(): StepDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(migration_2_3)
                    .fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }

        }

    }
}

    val migration_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Here you can write SQL statements to update the database schema.
            // In this case, you need to change the data type of the 'step_no' column.
            database.execSQL("ALTER TABLE step_data RENAME TO temp_step_data")
            database.execSQL("CREATE TABLE IF NOT EXISTS step_data (step_no TEXT NOT NULL, PRIMARY KEY(step_no))")
            database.execSQL("INSERT INTO step_data (step_no) SELECT step_no FROM temp_step_data")
            database.execSQL("DROP TABLE temp_step_data")
        }
}