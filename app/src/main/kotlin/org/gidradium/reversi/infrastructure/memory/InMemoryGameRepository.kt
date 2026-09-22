package org.gidradium.reversi.infrastructure.memory

import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.GameRecord
import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.application.repository.IGameRepository
import org.gidradium.reversi.game.GameSnapshot

class InMemoryGameRepository : IGameRepository {

    private val games = mutableMapOf<GameId, GameRecord>()
    private var nextId = 1

    override fun create(
        whitePlayerId: PlayerId,
        blackPlayerId: PlayerId,
        snapshot: GameSnapshot
    ): GameId {
        val id = GameId(nextId++)

        games[id] = GameRecord(
            id = id,
            whitePlayerId = whitePlayerId,
            blackPlayerId = blackPlayerId,
            snapshot = snapshot
        )

        return id
    }

    override fun update(
        id: GameId,
        snapshot: GameSnapshot
    ) {
        val game = requireNotNull(games[id]) {
            "Game with id ${id.value} does not exist"
        }

        games[id] = game.copy(
            snapshot = snapshot
        )
    }

    override fun findById(id: GameId): GameRecord? {
        return games[id]
    }

    override fun findAll(): List<GameRecord> {
        return games.values.toList()
    }

    override fun delete(id: GameId) {
        require(games.remove(id) != null) {
            "Game with id ${id.value} does not exist"
        }
    }
}
