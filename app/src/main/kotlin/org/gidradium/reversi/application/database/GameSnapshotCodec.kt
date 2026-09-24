package org.gidradium.reversi.application.database

import org.gidradium.reversi.game.Cell
import org.gidradium.reversi.game.GameSnapshot
import org.gidradium.reversi.game.Move
import org.gidradium.reversi.game.PlayerColor
import org.gidradium.reversi.game.Position

object GameSnapshotCodec {

    private const val BOARD_SIZE = 8

    fun encodeBoard(board: List<List<Cell>>): String {
        require(board.size == BOARD_SIZE) {
            "Board must have $BOARD_SIZE rows"
        }

        require(board.all { it.size == BOARD_SIZE }) {
            "Each board row must have $BOARD_SIZE cells"
        }

        return board
            .flatten()
            .joinToString("") { it.toDatabaseChar().toString() }
    }

    fun decodeBoard(value: String): List<List<Cell>> {
        require(value.length == BOARD_SIZE * BOARD_SIZE) {
            "Encoded board must contain 64 cells"
        }

        return value
            .map { it.toCell() }
            .chunked(BOARD_SIZE)
    }

    private fun Cell.toDatabaseChar(): Char =
        when (this) {
            Cell.EMPTY -> 'E'
            Cell.BLACK -> 'B'
            Cell.WHITE -> 'W'
        }

    private fun Char.toCell(): Cell =
        when (this) {
            'E' -> Cell.EMPTY
            'B' -> Cell.BLACK
            'W' -> Cell.WHITE
            else -> error("Unknown cell value: $this")
        }
}
