package org.gidradium.reversi.application

import org.gidradium.reversi.application.repository.IGameRepository
import org.gidradium.reversi.application.repository.IPlayerRepository
import org.gidradium.reversi.domain.game.Game
import org.gidradium.reversi.domain.game.GameSnapshot
import org.gidradium.reversi.domain.game.MoveEvaluation
import org.gidradium.reversi.domain.game.Position

class GameService(
    private val playerRepository: IPlayerRepository,
    private val gameRepository: IGameRepository
) {

    private val activeGames = mutableMapOf<Int, Game>()

    fun createGame(
        whitePlayerId: Int,
        blackPlayerId: Int
    ): Int {
        require(whitePlayerId != blackPlayerId) {
            "Game must have two different players"
        }

        requireNotNull(playerRepository.findById(whitePlayerId)) {
            "White player does not exist"
        }

        requireNotNull(playerRepository.findById(blackPlayerId)) {
            "Black player does not exist"
        }

        val game = Game()

        val gameId = gameRepository.create(
            whitePlayerId = whitePlayerId,
            blackPlayerId = blackPlayerId,
            snapshot = game.snapshot()
        )

        activeGames[gameId] = game

        return gameId
    }

    fun deleteGame(gameId: Int) {
        requireNotNull(gameRepository.findById(gameId)) {
            "Game with id $gameId does not exist"
        }

        activeGames.remove(gameId)
        gameRepository.delete(gameId)
    }

    fun validateMove(
        gameId: Int,
        position: Position
    ): MoveEvaluation {
        return getGame(gameId).validateMove(position)
    }

    fun makeMove(
        gameId: Int,
        position: Position
    ): MoveEvaluation {
        val game = getGame(gameId)

        val result = game.makeMove(position)

        if (result.isValid) {
            gameRepository.update(
                id = gameId,
                snapshot = game.snapshot()
            )
        }

        return result
    }

    fun getAvailableMoves(gameId: Int): Set<Position> {
        return getGame(gameId).availableMoves()
    }

    fun getGameSnapshot(gameId: Int): GameSnapshot {
        return getGame(gameId).snapshot()
    }

    fun getGameInfo(gameId: Int): GameInfo {
        val storedGame = requireNotNull(gameRepository.findById(gameId)) {
            "Game with id $gameId does not exist"
        }

        val snapshot = activeGames[gameId]?.snapshot() ?: storedGame.snapshot

        return GameInfo(
            id = storedGame.id,
            whitePlayerId = storedGame.whitePlayerId,
            blackPlayerId = storedGame.blackPlayerId,
            snapshot = snapshot
        )
    }

    fun getAllGames(): List<GameInfo> {
        return gameRepository.findAll().map { storedGame ->
            GameInfo(
                id = storedGame.id,
                whitePlayerId = storedGame.whitePlayerId,
                blackPlayerId = storedGame.blackPlayerId,
                snapshot = activeGames[storedGame.id]?.snapshot()
                    ?: storedGame.snapshot
            )
        }
    }

    private fun getGame(gameId: Int): Game {
        return requireNotNull(activeGames[gameId]) {
            "Game with id $gameId is not active"
        }
    }
}
