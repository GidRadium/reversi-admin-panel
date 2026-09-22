package org.gidradium.reversi.application

import org.gidradium.reversi.game.Board
import org.gidradium.reversi.game.GameSnapshot
import org.gidradium.reversi.game.GameStatus
import org.gidradium.reversi.game.PlayerColor
import org.gidradium.reversi.game.Position
import org.gidradium.reversi.application.repository.InMemoryGameRepository
import org.gidradium.reversi.application.repository.InMemoryPlayerRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AdminServiceTest {

    private fun createService(): AdminService {
        return AdminService(
            playerRepository = InMemoryPlayerRepository(),
            gameRepository = InMemoryGameRepository()
        )
    }

    @Test
    fun `create player returns id and stores name`() {
        val service = createService()

        val playerId = service.createPlayer("Alice")

        assertEquals("Alice", service.getPlayer(playerId))
    }

    @Test
    fun `get players returns all registered players`() {
        val service = createService()

        val alice = service.createPlayer("Alice")
        val bob = service.createPlayer("Bob")

        val players = service.getPlayers()

        assertEquals(
            mapOf(
                alice to "Alice",
                bob to "Bob"
            ),
            players
        )
    }

    @Test
    fun `get player returns null for unknown id`() {
        val service = createService()

        assertNull(service.getPlayer(PlayerId(999)))
    }

    @Test
    fun `delete player removes player`() {
        val service = createService()

        val playerId = service.createPlayer("Alice")

        service.deletePlayer(playerId)

        assertNull(service.getPlayer(playerId))
    }

    @Test
    fun `delete player is rejected when player participates in game`() {
        val service = createService()

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")

        service.createGame(whiteId, blackId)

        assertThrows<IllegalArgumentException> {
            service.deletePlayer(whiteId)
        }
    }

    @Test
    fun `player statistics count finished wins and losses`() {
        val playerRepository = InMemoryPlayerRepository()
        val gameRepository = InMemoryGameRepository()
        val service = AdminService(playerRepository, gameRepository)

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")

        val finishedGame = GameSnapshot(
            board = Board().snapshot(),
            currentPlayer = PlayerColor.BLACK,
            history = emptyList(),
            status = GameStatus.FINISHED,
            winner = PlayerColor.BLACK
        )

        gameRepository.create(
            whitePlayerId = whiteId,
            blackPlayerId = blackId,
            snapshot = finishedGame
        )

        val whiteStatistics = service.getPlayerStatistics(whiteId)
        val blackStatistics = service.getPlayerStatistics(blackId)

        assertEquals(1, whiteStatistics.gamesPlayed)
        assertEquals(0, whiteStatistics.wins)
        assertEquals(1, whiteStatistics.losses)
        assertEquals(0, whiteStatistics.draws)

        assertEquals(1, blackStatistics.gamesPlayed)
        assertEquals(1, blackStatistics.wins)
        assertEquals(0, blackStatistics.losses)
        assertEquals(0, blackStatistics.draws)
    }

    @Test
    fun `create game returns id and stores initial snapshot`() {
        val service = createService()

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")

        val gameId = service.createGame(whiteId, blackId)
        val game = service.getGameRecord(gameId)

        assertEquals(gameId, game.id)
        assertEquals(whiteId, game.whitePlayerId)
        assertEquals(blackId, game.blackPlayerId)
        assertEquals(GameStatus.IN_PROGRESS, game.snapshot.status)
        assertEquals(0, game.snapshot.history.size)
    }

    @Test
    fun `create game rejects same player`() {
        val service = createService()

        val playerId = service.createPlayer("Alice")

        assertThrows<IllegalArgumentException> {
            service.createGame(playerId, playerId)
        }
    }

    @Test
    fun `validate move does not change game`() {
        val service = createService()

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")
        val gameId = service.createGame(whiteId, blackId)

        val before = service.getGameSnapshot(gameId)

        val result = service.validateMove(
            gameId,
            Position(2, 3)
        )

        val after = service.getGameSnapshot(gameId)

        assertTrue(result.isValid)
        assertEquals(before, after)
    }

    @Test
    fun `make move changes game and persists snapshot`() {
        val service = createService()

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")
        val gameId = service.createGame(whiteId, blackId)

        val result = service.makeMove(
            gameId,
            Position(2, 3)
        )

        val snapshot = service.getGameSnapshot(gameId)

        assertTrue(result.isValid)
        assertEquals(1, snapshot.history.size)
        assertEquals(PlayerColor.WHITE, snapshot.currentPlayer)
    }

    @Test
    fun `available moves returns valid moves for current player`() {
        val service = createService()

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")
        val gameId = service.createGame(whiteId, blackId)

        val moves = service.getAvailableMoves(gameId)

        assertEquals(4, moves.size)
        assertTrue(Position(2, 3) in moves)
        assertTrue(Position(3, 2) in moves)
        assertTrue(Position(4, 5) in moves)
        assertTrue(Position(5, 4) in moves)
    }

    @Test
    fun `delete game removes game`() {
        val service = createService()

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")
        val gameId = service.createGame(whiteId, blackId)

        service.deleteGame(gameId)

        assertThrows<IllegalArgumentException> {
            service.getGameRecord(gameId)
        }
    }

    @Test
    fun `get games returns all games`() {
        val service = createService()

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")

        val firstGame = service.createGame(whiteId, blackId)

        val secondWhiteId = service.createPlayer("Carol")
        val secondGame = service.createGame(secondWhiteId, blackId)

        val games = service.getGames()

        assertEquals(
            setOf(firstGame, secondGame),
            games.map { it.id }.toSet()
        )
    }

    @Test
    fun `make invalid move does not update repository`() {
        val service = createService()

        val whiteId = service.createPlayer("Alice")
        val blackId = service.createPlayer("Bob")
        val gameId = service.createGame(whiteId, blackId)

        val before = service.getGameSnapshot(gameId)

        val result = service.makeMove(
            gameId,
            Position(0, 0)
        )

        val after = service.getGameSnapshot(gameId)

        assertFalse(result.isValid)
        assertEquals(before, after)
    }
}
