package org.gidradium.reversi.game

data class GameSnapshot(
    val board: List<List<Cell>>,
    val currentPlayer: PlayerColor,
    val history: List<Move>,
    val status: GameStatus,
    val winner: PlayerColor?
)
