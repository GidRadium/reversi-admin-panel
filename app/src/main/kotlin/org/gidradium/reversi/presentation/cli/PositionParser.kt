package org.gidradium.reversi.presentation.cli

import org.gidradium.reversi.game.Position

object PositionParser {

    fun parse(input: String): Position {
        require(input.length == 2) {
            "Position must have format like D3"
        }

        val column = input[0].uppercaseChar() - 'A'

        val row = input[1].digitToIntOrNull()?.minus(1)
            ?: throw IllegalArgumentException("Invalid row")

        return Position(row, column)
    }
}
