package com.example.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class,
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SonoraDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: SonoraDatabase? = null

        fun getInstance(context: Context): SonoraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SonoraDatabase::class.java,
                    "sonora_music.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
