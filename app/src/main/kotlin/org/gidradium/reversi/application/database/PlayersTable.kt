package org.gidradium.reversi.application.database

import org.jetbrains.exposed.v1.core.Table

object PlayersTable : Table("players") {

    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)

    override val primaryKey = PrimaryKey(id)
}
