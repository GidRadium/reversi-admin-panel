package org.gidradium.reversi.application.database

import org.gidradium.reversi.game.PlayerColor
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object MovesTable : Table("moves") {

    val id = integer("id").autoIncrement()

    val gameId = integer("game_id").references(
        ref = GamesTable.id,
        onDelete = ReferenceOption.CASCADE
    )

    val moveNumber = integer("move_number")

    val row = integer("row")
    val column = integer("column")

    val player = enumerationByName(
        name = "player",
        length = 10,
        klass = PlayerColor::class
    )

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(gameId, moveNumber)
    }
}
