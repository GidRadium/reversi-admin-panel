package org.gidradium.reversi.presentation.gui

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.repository.InMemoryGameRepository
import org.gidradium.reversi.application.repository.InMemoryPlayerRepository
import org.gidradium.reversi.game.GameStatus
import org.gidradium.reversi.game.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GuiViewModelTest {

    private fun createViewModel(): GuiViewModel {
        return GuiViewModel(
            AdminService(
                playerRepository = InMemoryPlayerRepository(),
                gameRepository = InMemoryGameRepository()
            )
        )
    }

    @Test
    fun `refresh loads players and games`() {
        val viewModel = createViewModel()

        viewModel.refresh()

        assertTrue(viewModel.state.players.isEmpty())
        assertTrue(viewModel.state.games.isEmpty())
    }

    @Test
    fun `create player adds player to state`() {
        val viewModel = createViewModel()

        viewModel.createPlayer("Alice")

        val playerId = viewModel.state.selectedPlayerId

        assertTrue(playerId != null)
        assertEquals("Alice", viewModel.state.players[playerId])
        assertEquals("Created player #${playerId!!.value}", viewModel.state.message)
    }

    @Test
    fun `select player loads statistics`() {
        val viewModel = createViewModel()

        viewModel.createPlayer("Alice")
        val playerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.selectPlayer(playerId)

        val statistics = requireNotNull(viewModel.state.selectedPlayerStatistics)

        assertEquals(0, statistics.gamesPlayed)
        assertEquals(0, statistics.wins)
        assertEquals(0, statistics.losses)
        assertEquals(0, statistics.draws)
    }

    @Test
    fun `delete selected player removes player from state`() {
        val viewModel = createViewModel()

        viewModel.createPlayer("Alice")
        val playerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.deleteSelectedPlayer()

        assertFalse(viewModel.state.players.containsKey(playerId))
        assertEquals(null, viewModel.state.selectedPlayerId)
        assertEquals(null, viewModel.state.selectedPlayerStatistics)
        assertEquals("Deleted player #${playerId.value}", viewModel.state.message)
    }

    @Test
    fun `create game adds game to state`() {
        val viewModel = createViewModel()

        viewModel.createPlayer("Alice")
        val whitePlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createPlayer("Bob")
        val blackPlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createGame(
            whitePlayerId = whitePlayerId,
            blackPlayerId = blackPlayerId
        )

        val gameId = requireNotNull(viewModel.state.selectedGameId)
        val game = viewModel.state.games.single { it.id == gameId }

        assertEquals(whitePlayerId, game.whitePlayerId)
        assertEquals(blackPlayerId, game.blackPlayerId)
        assertEquals(GameStatus.IN_PROGRESS, game.snapshot.status)
    }

    @Test
    fun `select game loads available moves`() {
        val viewModel = createViewModel()

        viewModel.createPlayer("Alice")
        val whitePlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createPlayer("Bob")
        val blackPlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createGame(whitePlayerId, blackPlayerId)

        val gameId = requireNotNull(viewModel.state.selectedGameId)

        viewModel.selectGame(gameId)

        assertEquals(4, viewModel.state.availableMoves.size)
        assertTrue(Position(2, 3) in viewModel.state.availableMoves)
    }

    @Test
    fun `make valid move updates game state`() {
        val viewModel = createViewModel()

        viewModel.createPlayer("Alice")
        val whitePlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createPlayer("Bob")
        val blackPlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createGame(whitePlayerId, blackPlayerId)

        viewModel.makeMove(Position(2, 3))

        val gameId = requireNotNull(viewModel.state.selectedGameId)
        val game = viewModel.state.games.single { it.id == gameId }

        assertEquals(1, game.snapshot.history.size)
        assertEquals("Move accepted", viewModel.state.message)
        assertTrue(viewModel.state.lastMoveEvaluation?.isValid == true)
    }

    @Test
    fun `make invalid move keeps available moves`() {
        val viewModel = createViewModel()

        viewModel.createPlayer("Alice")
        val whitePlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createPlayer("Bob")
        val blackPlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createGame(whitePlayerId, blackPlayerId)

        val availableMovesBefore = viewModel.state.availableMoves

        viewModel.makeMove(Position(0, 0))

        assertEquals(
            availableMovesBefore,
            viewModel.state.availableMoves
        )
        assertEquals(
            "Move rejected: Move does not capture any opponent pieces",
            viewModel.state.message
        )
        assertFalse(
            requireNotNull(viewModel.state.lastMoveEvaluation).isValid
        )
    }

    @Test
    fun `delete selected game removes game from state`() {
        val viewModel = createViewModel()

        viewModel.createPlayer("Alice")
        val whitePlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createPlayer("Bob")
        val blackPlayerId = requireNotNull(viewModel.state.selectedPlayerId)

        viewModel.createGame(whitePlayerId, blackPlayerId)

        val gameId = requireNotNull(viewModel.state.selectedGameId)

        viewModel.deleteSelectedGame()

        assertFalse(
            viewModel.state.games.any { it.id == gameId }
        )
        assertEquals(null, viewModel.state.selectedGameId)
        assertEquals(
            emptySet<Position>(),
            viewModel.state.availableMoves
        )
        assertEquals(
            "Deleted game #${gameId.value}",
            viewModel.state.message
        )
    }

    @Test
    fun `delete player without selection is rejected`() {
        val viewModel = createViewModel()

        assertThrows<IllegalArgumentException> {
            viewModel.deleteSelectedPlayer()
        }
    }

    @Test
    fun `delete game without selection is rejected`() {
        val viewModel = createViewModel()

        assertThrows<IllegalArgumentException> {
            viewModel.deleteSelectedGame()
        }
    }
}
