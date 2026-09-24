package org.gidradium.reversi.application.database

import org.gidradium.reversi.game.GameStatus
import org.gidradium.reversi.game.PlayerColor
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table

object GamesTable : Table("games") {

    val id = integer("id").autoIncrement()

    val whitePlayerId = integer("white_player_id").references(
        ref = PlayersTable.id,
        onDelete = ReferenceOption.RESTRICT
    )

    val blackPlayerId = integer("black_player_id").references(
        ref = PlayersTable.id,
        onDelete = ReferenceOption.RESTRICT
    )

    val board = varchar("board", 64)

    val currentPlayer = enumerationByName(
        name = "current_player",
        length = 10,
        klass = PlayerColor::class
    )

    val status = enumerationByName(
        name = "status",
        length = 20,
        klass = GameStatus::class
    )

    val winner = enumerationByName(
        name = "winner",
        length = 10,
        klass = PlayerColor::class
    ).nullable()

    override val primaryKey = PrimaryKey(id)
}
