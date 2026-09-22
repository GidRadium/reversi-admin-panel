package org.gidradium.reversi.domain.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BoardTest {

    @Test
    fun `new board has empty cells except initial pieces`() {
        val board = Board()

        for (row in 0 until Board.SIZE) {
            for (column in 0 until Board.SIZE) {
                val position = Position(row, column)

                val expected = when (position) {
                    Position(3, 3) -> Cell.WHITE
                    Position(3, 4) -> Cell.BLACK
                    Position(4, 3) -> Cell.BLACK
                    Position(4, 4) -> Cell.WHITE
                    else -> Cell.EMPTY
                }

                assertEquals(expected, board[position])
            }
        }
    }
}
