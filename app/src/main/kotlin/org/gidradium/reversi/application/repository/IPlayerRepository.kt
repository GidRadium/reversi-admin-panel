package org.gidradium.reversi.application.repository

import org.gidradium.reversi.domain.player.Player

interface IPlayerRepository {
    fun create(name: String): Player
    fun findById(id: Int): Player?
    fun findAll(): List<Player>
}
