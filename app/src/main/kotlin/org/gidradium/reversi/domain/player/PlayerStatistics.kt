package org.gidradium.reversi.domain.player

data class PlayerStatistics(
    val gamesPlayed: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int
)
