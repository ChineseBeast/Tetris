package com.example.tetris.logic.local.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_records")
data class GameRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    /** 游戏日期时间戳（毫秒） */
    val playDate: Long,
    /** 游戏时长（秒） */
    val duration: Int,
    /** 最终得分 */
    val score: Int,
    /** 备注文本 */
    val note: String = ""
)
