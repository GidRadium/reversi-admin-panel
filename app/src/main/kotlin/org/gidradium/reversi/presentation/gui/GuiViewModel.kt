package org.gidradium.reversi.presentation.gui

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.game.Position

class GuiViewModel(
    private val adminService: AdminService
) {

    var state: GuiState = GuiState()
        private set

    fun refresh() {
        state = state.copy(
            players = adminService.getPlayers(),
            games = adminService.getGames()
        )
    }

    fun createPlayer(name: String) {
        val playerId = adminService.createPlayer(name)

        state = state.copy(
            selectedPlayerId = playerId,
            message = "Created player #${playerId.value}"
        )

        refresh()
    }

    fun selectPlayer(playerId: PlayerId) {
        state = state.copy(
            selectedPlayerId = playerId,
            selectedPlayerStatistics = adminService.getPlayerStatistics(playerId)
        )
    }

    fun deleteSelectedPlayer() {
        val playerId = requireNotNull(state.selectedPlayerId) {
            "No player selected"
        }

        adminService.deletePlayer(playerId)

        state = state.copy(
            selectedPlayerId = null,
            selectedPlayerStatistics = null,
            message = "Deleted player #${playerId.value}"
        )

        refresh()
    }

    fun createGame(
        whitePlayerId: PlayerId,
        blackPlayerId: PlayerId
    ) {
        val gameId = adminService.createGame(
            whitePlayerId = whitePlayerId,
            blackPlayerId = blackPlayerId
        )

        state = state.copy(
            selectedGameId = gameId,
            availableMoves = adminService.getAvailableMoves(gameId),
            message = "Created game #${gameId.value}"
        )

        refresh()
    }

    fun selectGame(gameId: GameId) {
        state = state.copy(
            selectedGameId = gameId,
            availableMoves = adminService.getAvailableMoves(gameId)
        )
    }

    fun deleteSelectedGame() {
        val gameId = requireNotNull(state.selectedGameId) {
            "No game selected"
        }

        adminService.deleteGame(gameId)

        state = state.copy(
            selectedGameId = null,
            availableMoves = emptySet(),
            message = "Deleted game #${gameId.value}"
        )

        refresh()
    }

    fun makeMove(position: Position) {
        val gameId = requireNotNull(state.selectedGameId) {
            "No game selected"
        }

        val result = adminService.makeMove(
            gameId = gameId,
            position = position
        )

        state = state.copy(
            availableMoves = if (result.isValid) {
                adminService.getAvailableMoves(gameId)
            } else {
                state.availableMoves
            },
            lastMoveEvaluation = result,
            message = if (result.isValid) {
                "Move accepted"
            } else {
                "Move rejected: ${result.reason}"
            },
            games = adminService.getGames()
        )
    }
}
