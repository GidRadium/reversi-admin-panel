package org.gidradium.reversi.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameSnapshotTest {

    @Test
    fun `game can be restored from snapshot`() {
        val originalGame = Game()

        originalGame.makeMove(Position(2, 3))
        originalGame.makeMove(Position(2, 2))

        val snapshot = originalGame.snapshot()

        val restoredGame = Game.fromSnapshot(snapshot)

        assertEquals(snapshot, restoredGame.snapshot())
    }

    @Test
    fun `restored game can continue playing`() {
        val originalGame = Game()

        originalGame.makeMove(Position(2, 3))

        val snapshot = originalGame.snapshot()

        val restoredGame = Game.fromSnapshot(snapshot)

        val availableMoves = restoredGame.availableMoves()

        assertEquals(
            originalGame.availableMoves(),
            availableMoves
        )

        val result = restoredGame.makeMove(
            availableMoves.first()
        )

        assertTrue(result.isValid)
        assertEquals(
            snapshot.history.size + 1,
            restoredGame.snapshot().history.size
        )
    }

    @Test
    fun `finished game remains finished after restoration`() {
        val snapshot = GameSnapshot(
            board = Board().snapshot(),
            currentPlayer = PlayerColor.BLACK,
            history = emptyList(),
            status = GameStatus.FINISHED,
            winner = PlayerColor.WHITE
        )

        val game = Game.fromSnapshot(snapshot)

        assertEquals(GameStatus.FINISHED, game.snapshot().status)
        assertEquals(PlayerColor.WHITE, game.snapshot().winner)
        assertTrue(game.availableMoves().isEmpty())

        val result = game.validateMove(Position(2, 3))

        assertEquals(false, result.isValid)
        assertEquals("Game is already finished", result.reason)
    }
}
