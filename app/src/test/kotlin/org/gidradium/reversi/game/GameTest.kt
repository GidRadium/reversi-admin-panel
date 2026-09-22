package org.gidradium.reversi.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameTest {

    @Test
    fun `new game starts with black`() {
        val game = Game()

        val snapshot = game.snapshot()

        assertEquals(PlayerColor.BLACK, snapshot.currentPlayer)
        assertEquals(GameStatus.IN_PROGRESS, snapshot.status)
        assertTrue(snapshot.history.isEmpty())
        assertEquals(null, snapshot.winner)
    }

    @Test
    fun `new game has standard initial board`() {
        val game = Game()

        val board = game.snapshot().board

        assertEquals(Cell.WHITE, board[3][3])
        assertEquals(Cell.BLACK, board[3][4])
        assertEquals(Cell.BLACK, board[4][3])
        assertEquals(Cell.WHITE, board[4][4])
    }

    @Test
    fun `new game has four available moves for black`() {
        val game = Game()

        val expected = setOf(
            Position(2, 3),
            Position(3, 2),
            Position(4, 5),
            Position(5, 4)
        )

        assertEquals(expected, game.availableMoves())
    }

    @Test
    fun `valid move changes board and history`() {
        val game = Game()

        val result = game.makeMove(Position(2, 3))
        val snapshot = game.snapshot()

        assertTrue(result.isValid)

        assertEquals(Cell.BLACK, snapshot.board[2][3])
        assertEquals(Cell.BLACK, snapshot.board[3][3])

        assertEquals(
            listOf(Move(Position(2, 3), PlayerColor.BLACK)),
            snapshot.history
        )
    }

    @Test
    fun `valid move changes current player`() {
        val game = Game()

        game.makeMove(Position(2, 3))

        assertEquals(
            PlayerColor.WHITE,
            game.snapshot().currentPlayer
        )
    }

    @Test
    fun `invalid move does not change game`() {
        val game = Game()

        val before = game.snapshot()

        val result = game.makeMove(Position(3, 3))

        val after = game.snapshot()

        assertFalse(result.isValid)
        assertEquals(before, after)
    }

    @Test
    fun `validate move does not change game`() {
        val game = Game()

        val before = game.snapshot()

        game.validateMove(Position(2, 3))

        val after = game.snapshot()

        assertEquals(before, after)
    }

    @Test
    fun `available moves do not change game`() {
        val game = Game()

        val before = game.snapshot()

        game.availableMoves()

        val after = game.snapshot()

        assertEquals(before, after)
    }
}
