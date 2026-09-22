package org.gidradium.reversi.application.repository

import org.gidradium.reversi.domain.game.GameSnapshot

data class StoredGame(
    val id: Int,
    val whitePlayerId: Int,
    val blackPlayerId: Int,
    val snapshot: GameSnapshot
)

interface IGameRepository {
    fun create(
        whitePlayerId: Int,
        blackPlayerId: Int,
        snapshot: GameSnapshot
    ): Int

    fun update(id: Int, snapshot: GameSnapshot)

    fun findById(id: Int): StoredGame?

    fun findAll(): List<StoredGame>

    fun delete(id: Int)
}
