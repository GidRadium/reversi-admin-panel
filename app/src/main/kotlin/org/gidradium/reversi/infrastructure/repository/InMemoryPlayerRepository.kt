package org.gidradium.reversi.infrastructure.repository

import org.gidradium.reversi.application.repository.IPlayerRepository
import org.gidradium.reversi.domain.player.Player

class InMemoryPlayerRepository : IPlayerRepository {

    private val players = mutableMapOf<Int, Player>()
    private var nextId = 1

    override fun create(name: String): Player {
        val player = Player(
            id = nextId,
            name = name
        )

        players[nextId] = player
        nextId++

        return player
    }

    override fun findById(id: Int): Player? {
        return players[id]
    }

    override fun findAll(): List<Player> {
        return players.values.toList()
    }
}
