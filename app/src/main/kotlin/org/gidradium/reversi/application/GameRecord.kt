package org.gidradium.reversi.application

import org.gidradium.reversi.game.GameSnapshot

data class GameRecord(
    val id: GameId,
    val whitePlayerId: PlayerId,
    val blackPlayerId: PlayerId,
    val snapshot: GameSnapshot
)
