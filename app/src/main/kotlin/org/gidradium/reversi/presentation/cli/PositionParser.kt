package org.gidradium.reversi.presentation.cli

import org.gidradium.reversi.domain.game.Position

object PositionParser {

    fun parse(value: String): Position {
        require(value.length == 2) {
            "Position must have format like D3"
        }

        val column = value[0].uppercaseChar() - 'A'

        val row = value[1].digitToIntOrNull()?.minus(1)
            ?: throw IllegalArgumentException("Invalid row")

        return Position(row, column)
    }
}
