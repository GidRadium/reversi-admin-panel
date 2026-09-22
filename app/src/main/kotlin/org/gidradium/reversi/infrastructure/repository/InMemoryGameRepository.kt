package org.gidradium.reversi.infrastructure.repository

import org.gidradium.reversi.application.repository.IGameRepository
import org.gidradium.reversi.application.repository.StoredGame
import org.gidradium.reversi.domain.game.GameSnapshot

class InMemoryGameRepository : IGameRepository {

    private val games = mutableMapOf<Int, StoredGame>()
    private var nextId = 1

    override fun create(
        whitePlayerId: Int,
        blackPlayerId: Int,
        snapshot: GameSnapshot
    ): Int {
        val id = nextId

        games[id] = StoredGame(
            id = id,
            whitePlayerId = whitePlayerId,
            blackPlayerId = blackPlayerId,
            snapshot = snapshot
        )

        nextId++

        return id
    }

    override fun update(id: Int, snapshot: GameSnapshot) {
        val current = requireNotNull(games[id]) {
            "Game with id $id does not exist"
        }

        games[id] = current.copy(snapshot = snapshot)
    }

    override fun findById(id: Int): StoredGame? {
        return games[id]
    }

    override fun findAll(): List<StoredGame> {
        return games.values.toList()
    }
}
