package org.gidradium.reversi.application

import org.gidradium.reversi.application.repository.IPlayerRepository
import org.gidradium.reversi.domain.player.Player

class PlayerService(
    private val playerRepository: IPlayerRepository
) {
    fun createPlayer(name: String): Player {
        return playerRepository.create(name)
    }

    fun getPlayer(id: Int): Player? {
        return playerRepository.findById(id)
    }

    fun getAllPlayers(): List<Player> {
        return playerRepository.findAll()
    }
}
