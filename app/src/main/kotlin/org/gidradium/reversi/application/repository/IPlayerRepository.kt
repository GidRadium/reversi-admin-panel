package org.gidradium.reversi.application.repository

import org.gidradium.reversi.application.PlayerId

interface IPlayerRepository {
    fun create(name: String): PlayerId
    fun findById(id: PlayerId): String?
    fun findAll(): Map<PlayerId, String>
    fun delete(id: PlayerId)
}
