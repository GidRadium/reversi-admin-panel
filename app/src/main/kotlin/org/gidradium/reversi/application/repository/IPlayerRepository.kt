package org.gidradium.reversi.application.repository

import org.gidradium.reversi.application.PlayerId

interface IPlayerRepository {

    fun create(name: String): PlayerId

    fun findById(playerId: PlayerId): String?

    fun findAll(): Map<PlayerId, String>

    fun delete(playerId: PlayerId)
}
