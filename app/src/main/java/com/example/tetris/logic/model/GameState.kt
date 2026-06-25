package com.example.myapplication.data.model

/**
 * 游戏状态机。
 *
 *  Idle      — 进入游戏后尚未开始的初始状态
 *  Playing   — 游戏进行中
 *  Paused    — 暂停
 *  GameOver  — 游戏结束（携带最终得分）
 */
sealed class GameState {
    // 空闲状态 未开始
    data object Idle : GameState()
    // 游戏中状态
    data object Playing : GameState()
    // 暂停状态
    data object Paused : GameState()
    // 游戏结算状态
    data class GameOver(val score: Int) : GameState()
}