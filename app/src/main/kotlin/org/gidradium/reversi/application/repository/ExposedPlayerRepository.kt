package org.gidradium.reversi.application.repository

import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.application.database.PlayersTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class ExposedPlayerRepository(
    private val database: Database
) : IPlayerRepository {

    override fun create(name: String): PlayerId {
        require(name.isNotBlank()) {
            "Player name must not be blank"
        }

        val playerId = transaction(database) {
            PlayersTable
                .insert {
                    it[PlayersTable.name] = name
                }[PlayersTable.id]
        }

        return PlayerId(playerId)
    }

    override fun findById(playerId: PlayerId): String? =
        transaction(database) {
            PlayersTable
                .selectAll()
                .where { PlayersTable.id eq playerId.value }
                .map { it[PlayersTable.name] }
                .singleOrNull()
        }

    override fun findAll(): Map<PlayerId, String> =
        transaction(database) {
            PlayersTable
                .selectAll()
                .associate {
                    PlayerId(it[PlayersTable.id]) to it[PlayersTable.name]
                }
        }

    override fun delete(playerId: PlayerId) {
        val deletedRows = transaction(database) {
            PlayersTable.deleteWhere {
                PlayersTable.id eq playerId.value
            }
        }

        require(deletedRows == 1) {
            "Player with id ${playerId.value} does not exist"
        }
    }
}
