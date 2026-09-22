package org.gidradium.reversi.domain.game

data class GameProgress(
    val currentPlayer: PlayerColor,
    val status: GameStatus,
    val winner: PlayerColor?
)
