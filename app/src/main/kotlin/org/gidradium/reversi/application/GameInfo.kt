package org.gidradium.reversi.application

import org.gidradium.reversi.domain.game.GameSnapshot

data class GameInfo(
    val id: Int,
    val whitePlayerId: Int,
    val blackPlayerId: Int,
    val snapshot: GameSnapshot
)
