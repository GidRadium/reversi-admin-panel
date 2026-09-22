package org.gidradium.reversi.application.repository

import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.GameRecord
import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.game.GameSnapshot

interface IGameRepository {

    fun create(
        whitePlayerId: PlayerId,
        blackPlayerId: PlayerId,
        snapshot: GameSnapshot
    ): GameId

    fun update(
        gameId: GameId,
        snapshot: GameSnapshot
    )

    fun findById(gameId: GameId): GameRecord?

    fun findAll(): List<GameRecord>

    fun delete(gameId: GameId)
}
