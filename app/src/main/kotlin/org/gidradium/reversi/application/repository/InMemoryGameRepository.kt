package org.gidradium.reversi.application.repository

import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.GameRecord
import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.game.GameSnapshot

class InMemoryGameRepository : IGameRepository {

    private val games = mutableMapOf<GameId, GameRecord>()
    private var nextId = 1

    override fun create(
        whitePlayerId: PlayerId,
        blackPlayerId: PlayerId,
        snapshot: GameSnapshot
    ): GameId {
        val gameId = GameId(nextId++)

        games[gameId] = GameRecord(
            id = gameId,
            whitePlayerId = whitePlayerId,
            blackPlayerId = blackPlayerId,
            snapshot = snapshot
        )

        return gameId
    }

    override fun update(
        gameId: GameId,
        snapshot: GameSnapshot
    ) {
        val game = requireNotNull(games[gameId]) {
            "Game with id ${gameId.value} does not exist"
        }

        games[gameId] = game.copy(
            snapshot = snapshot
        )
    }

    override fun findById(gameId: GameId): GameRecord? =
        games[gameId]

    override fun findAll(): List<GameRecord> =
        games.values.toList()

    override fun delete(gameId: GameId) {
        require(games.remove(gameId) != null) {
            "Game with id ${gameId.value} does not exist"
        }
    }
}
