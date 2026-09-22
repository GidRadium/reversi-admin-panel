package org.gidradium.reversi.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReversiRulesTest {

    private val rules = ReversiRules()

    @Test
    fun `black can make standard opening move`() {
        val board = Board()

        val result = rules.evaluateMove(
            board = board,
            player = PlayerColor.BLACK,
            position = Position(2, 3)
        )

        assertTrue(result.isValid)
        assertEquals(
            listOf(Position(3, 3)),
            result.flippedCells
        )
    }

    @Test
    fun `cannot place piece on occupied cell`() {
        val board = Board()

        val result = rules.evaluateMove(
            board = board,
            player = PlayerColor.BLACK,
            position = Position(3, 3)
        )

        assertFalse(result.isValid)
        assertEquals(emptyList<Position>(), result.flippedCells)
    }

    @Test
    fun `move without captured opponent piece is invalid`() {
        val board = Board()

        val result = rules.evaluateMove(
            board = board,
            player = PlayerColor.BLACK,
            position = Position(0, 0)
        )

        assertFalse(result.isValid)
    }

    @Test
    fun `finds all black opening moves`() {
        val board = Board()

        val expected = setOf(
            Position(2, 3),
            Position(3, 2),
            Position(4, 5),
            Position(5, 4)
        )

        assertEquals(
            expected,
            rules.availableMoves(board, PlayerColor.BLACK)
        )
    }

    @Test
    fun `rules do not modify board`() {
        val board = Board()

        rules.evaluateMove(
            board = board,
            player = PlayerColor.BLACK,
            position = Position(2, 3)
        )

        assertEquals(Cell.WHITE, board[Position(3, 3)])
        assertEquals(Cell.BLACK, board[Position(3, 4)])
    }

    @Test
    fun `after black opening move white becomes current player`() {
        val board = Board()

        val evaluation = ReversiRules().evaluateMove(
            board = board,
            player = PlayerColor.BLACK,
            position = Position(2, 3)
        )

        assertTrue(evaluation.isValid)

        board[Position(2, 3)] = Cell.BLACK

        for (position in evaluation.flippedCells) {
            board[position] = Cell.BLACK
        }

        val progress = ReversiRules().determineGameProgress(
            board = board,
            playerWhoMoved = PlayerColor.BLACK
        )

        assertEquals(PlayerColor.WHITE, progress.currentPlayer)
        assertEquals(GameStatus.IN_PROGRESS, progress.status)
        assertEquals(null, progress.winner)
    }
}
