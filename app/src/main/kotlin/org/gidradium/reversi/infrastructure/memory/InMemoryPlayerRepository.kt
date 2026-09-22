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

        val id = PlayerId(nextId++)
        players[id] = name
        return id
    }

    override fun findById(id: PlayerId): String? {
        return players[id]
    }

    override fun findAll(): Map<PlayerId, String> {
        return players.toMap()
    }

    override fun delete(id: PlayerId) {
        require(players.remove(id) != null) {
            "Player $id not found"
        }
    }
}
