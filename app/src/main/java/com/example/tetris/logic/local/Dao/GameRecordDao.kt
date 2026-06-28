package com.example.tetris.logic.local.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tetris.logic.local.Entity.GameRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface GameRecordDao {

    /**用来新增数据*/
    @Insert
    suspend fun insert(record: GameRecord)
    /** 用来修改备注 */
    @Update
    suspend fun update(record: GameRecord)

    /** 按日期降序查询所有记录 */
    @Query("SELECT * FROM game_records ORDER BY playDate DESC")
    fun getAllOrderByDate(): Flow<List<GameRecord>>


}