package org.gidradium.reversi.application

import org.gidradium.reversi.application.repository.IGameRepository
import org.gidradium.reversi.application.repository.IPlayerRepository
import org.gidradium.reversi.game.Game
import org.gidradium.reversi.game.GameSnapshot
import org.gidradium.reversi.game.GameStatus
import org.gidradium.reversi.game.MoveEvaluation
import org.gidradium.reversi.game.PlayerColor
import org.gidradium.reversi.game.Position

class AdminService(
    private val playerRepository: IPlayerRepository,
    private val gameRepository: IGameRepository
) {

    private val activeGames = mutableMapOf<GameId, Game>()

    // ---------- Players ----------

    fun createPlayer(name: String): PlayerId = playerRepository.create(name)

    fun getPlayer(id: PlayerId): String? {
        return playerRepository.findById(id)
    }

    fun getPlayers(): Map<PlayerId, String> {
        return playerRepository.findAll()
    }

    fun deletePlayer(id: PlayerId) {
        requireNotNull(playerRepository.findById(id)) {
            "Player with id ${id.value} does not exist"
        }

        val participatesInGame = gameRepository.findAll().any {
            it.whitePlayerId == id || it.blackPlayerId == id
        }

        require(!participatesInGame) {
            "Player cannot be deleted because they participate in a game"
        }

        playerRepository.delete(id)
    }

    fun getPlayerStatistics(id: PlayerId): PlayerStatistics {
        requireNotNull(playerRepository.findById(id)) {
            "Player with id ${id.value} does not exist"
        }

        var gamesPlayed = 0
        var wins = 0
        var losses = 0
        var draws = 0

        for (game in gameRepository.findAll()) {
            val color = when (id) {
                game.whitePlayerId -> PlayerColor.WHITE
                game.blackPlayerId -> PlayerColor.BLACK
                else -> continue
            }

            gamesPlayed++

            if (game.snapshot.status != GameStatus.FINISHED) {
                continue
            }

            when (game.snapshot.winner) {
                null -> draws++
                color -> wins++
                else -> losses++
            }
        }

        return PlayerStatistics(
            gamesPlayed = gamesPlayed,
            wins = wins,
            losses = losses,
            draws = draws
        )
    }

    // ---------- Games ----------

    fun createGame(
        whitePlayerId: PlayerId,
        blackPlayerId: PlayerId
    ): GameId {
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

    fun deleteGame(id: GameId) {
        requireNotNull(gameRepository.findById(id)) {
            "Game with id ${id.value} does not exist"
        }

        activeGames.remove(id)
        gameRepository.delete(id)
    }

    fun validateMove(
        gameId: GameId,
        position: Position
    ): MoveEvaluation {
        return getActiveGame(gameId).validateMove(position)
    }

    fun makeMove(
        gameId: GameId,
        position: Position
    ): MoveEvaluation {
        val game = getActiveGame(gameId)

        val result = game.makeMove(position)

        if (result.isValid) {
            gameRepository.update(
                id = gameId,
                snapshot = game.snapshot()
            )
        }

        return result
    }

    fun getAvailableMoves(gameId: GameId): Set<Position> {
        return getActiveGame(gameId).availableMoves()
    }

    fun getGameSnapshot(gameId: GameId): GameSnapshot {
        return getActiveGame(gameId).snapshot()
    }

    fun getGameRecord(gameId: GameId): GameRecord {
        val record = requireNotNull(gameRepository.findById(gameId)) {
            "Game with id ${gameId.value} does not exist"
        }

        val activeGame = activeGames[gameId]

        return if (activeGame == null) {
            record
        } else {
            record.copy(
                snapshot = activeGame.snapshot()
            )
        }
    }

    fun getGames(): List<GameRecord> {
        return gameRepository.findAll().map { record ->
            val activeGame = activeGames[record.id]

            if (activeGame == null) {
                record
            } else {
                record.copy(
                    snapshot = activeGame.snapshot()
                )
            }
        }
    }

    private fun getActiveGame(gameId: GameId): Game {
        return requireNotNull(activeGames[gameId]) {
            "Game with id ${gameId.value} is not active"
        }
    }
}
