package com.example.tetris.logic.local.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tetris.logic.local.Dao.GameRecordDao
import com.example.tetris.logic.local.Entity.GameRecord

@Database(entities = [GameRecord::class], version = 1, exportSchema = false)
abstract class TetrisDatabase : RoomDatabase() {

    abstract fun gameRecordDao(): GameRecordDao

    companion object {
        @Volatile
        private var INSTANCE: TetrisDatabase? = null

        fun getInstance(context: Context): TetrisDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TetrisDatabase::class.java,
                    "tetris_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}