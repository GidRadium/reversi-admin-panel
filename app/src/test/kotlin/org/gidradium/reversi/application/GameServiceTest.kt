package org.gidradium.reversi.application

import org.gidradium.reversi.domain.game.Cell
import org.gidradium.reversi.domain.game.GameStatus
import org.gidradium.reversi.domain.game.PlayerColor
import org.gidradium.reversi.domain.game.Position
import org.gidradium.reversi.infrastructure.repository.InMemoryGameRepository
import org.gidradium.reversi.infrastructure.repository.InMemoryPlayerRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GameServiceTest {

    private val playerRepository = InMemoryPlayerRepository()
    private val gameRepository = InMemoryGameRepository()

    private val playerService = PlayerService(playerRepository)

    private val gameService = GameService(
        playerRepository = playerRepository,
        gameRepository = gameRepository
    )

    private val whitePlayer = playerService.createPlayer("Alice")
    private val blackPlayer = playerService.createPlayer("Bob")

    @Test
    fun `creates game for existing players`() {
        val gameId = gameService.createGame(
            whitePlayerId = whitePlayer.id,
            blackPlayerId = blackPlayer.id
        )

        val snapshot = gameService.getGameSnapshot(gameId)

        assertEquals(GameStatus.IN_PROGRESS, snapshot.status)
        assertEquals(PlayerColor.BLACK, snapshot.currentPlayer)
    }

    @Test
    fun `cannot create game with same player twice`() {
        assertThrows<IllegalArgumentException> {
            gameService.createGame(
                whitePlayerId = whitePlayer.id,
                blackPlayerId = whitePlayer.id
            )
        }
    }

    @Test
    fun `cannot create game with missing player`() {
        assertThrows<IllegalArgumentException> {
            gameService.createGame(
                whitePlayerId = whitePlayer.id,
                blackPlayerId = 999
            )
        }
    }

    @Test
    fun `makes valid move`() {
        val gameId = gameService.createGame(
            whitePlayerId = whitePlayer.id,
            blackPlayerId = blackPlayer.id
        )

        val result = gameService.makeMove(
            gameId = gameId,
            position = Position(2, 3)
        )

        assertTrue(result.isValid)

        val snapshot = gameService.getGameSnapshot(gameId)

        assertEquals(Cell.BLACK, snapshot.board[2][3])
        assertEquals(PlayerColor.WHITE, snapshot.currentPlayer)
        assertEquals(1, snapshot.history.size)
    }

    @Test
    fun `invalid move does not change game`() {
        val gameId = gameService.createGame(
            whitePlayerId = whitePlayer.id,
            blackPlayerId = blackPlayer.id
        )

        val before = gameService.getGameSnapshot(gameId)

        val result = gameService.makeMove(
            gameId = gameId,
            position = Position(3, 3)
        )

        val after = gameService.getGameSnapshot(gameId)

        assertFalse(result.isValid)
        assertEquals(before, after)
    }

    @Test
    fun `returns available moves`() {
        val gameId = gameService.createGame(
            whitePlayerId = whitePlayer.id,
            blackPlayerId = blackPlayer.id
        )

        val expected = setOf(
            Position(2, 3),
            Position(3, 2),
            Position(4, 5),
            Position(5, 4)
        )

        assertEquals(
            expected,
            gameService.getAvailableMoves(gameId)
        )
    }

    @Test
    fun `updates stored snapshot after valid move`() {
        val gameId = gameService.createGame(
            whitePlayerId = whitePlayer.id,
            blackPlayerId = blackPlayer.id
        )

        gameService.makeMove(
            gameId = gameId,
            position = Position(2, 3)
        )

        val storedGame = gameRepository.findById(gameId)

        assertEquals(1, storedGame!!.snapshot.history.size)
        assertEquals(
            Cell.BLACK,
            storedGame.snapshot.board[2][3]
        )
    }
}
