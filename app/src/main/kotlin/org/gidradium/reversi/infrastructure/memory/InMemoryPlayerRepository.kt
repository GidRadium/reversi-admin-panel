package org.gidradium.reversi.infrastructure.memory

import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.application.repository.IPlayerRepository

class InMemoryPlayerRepository : IPlayerRepository {

    private val players = mutableMapOf<PlayerId, String>()
    private var nextId = 1

    override fun create(name: String): PlayerId {
        require(name.isNotBlank()) {
            "Player name must not be blank"
        }

        val playerId = PlayerId(nextId++)
        players[playerId] = name

        return playerId
    }

    override fun findById(playerId: PlayerId): String? =
        players[playerId]

    override fun findAll(): Map<PlayerId, String> =
        players.toMap()

    override fun delete(playerId: PlayerId) {
        require(players.remove(playerId) != null) {
            "Player with id ${playerId.value} does not exist"
        }
    }
}
