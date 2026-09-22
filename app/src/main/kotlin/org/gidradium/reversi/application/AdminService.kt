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

    fun createPlayer(name: String): PlayerId =
        playerRepository.create(name)

    fun getPlayer(playerId: PlayerId): String? =
        playerRepository.findById(playerId)

    fun getPlayers(): Map<PlayerId, String> =
        playerRepository.findAll()

    fun deletePlayer(playerId: PlayerId) {
        requireNotNull(playerRepository.findById(playerId)) {
            "Player with id ${playerId.value} does not exist"
        }

        val isParticipatingInGame = gameRepository.findAll().any {
            it.whitePlayerId == playerId || it.blackPlayerId == playerId
        }

        require(!isParticipatingInGame) {
            "Player cannot be deleted because they participate in a game"
        }

        playerRepository.delete(playerId)
    }

    fun getPlayerStatistics(playerId: PlayerId): PlayerStatistics {
        requireNotNull(playerRepository.findById(playerId)) {
            "Player with id ${playerId.value} does not exist"
        }

        var gamesPlayed = 0
        var wins = 0
        var losses = 0
        var draws = 0

        for (game in gameRepository.findAll()) {
            val color = when (playerId) {
                game.whitePlayerId -> PlayerColor.WHITE
                game.blackPlayerId -> PlayerColor.BLACK
                else -> continue
            }

            gamesPlayed++

            val snapshot = game.snapshot

            if (snapshot.status != GameStatus.FINISHED) {
                continue
            }

            when (snapshot.winner) {
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

    fun deleteGame(gameId: GameId) {
        requireNotNull(gameRepository.findById(gameId)) {
            "Game with id ${gameId.value} does not exist"
        }

        activeGames.remove(gameId)
        gameRepository.delete(gameId)
    }

    fun validateMove(
        gameId: GameId,
        position: Position
    ): MoveEvaluation =
        getActiveGame(gameId).validateMove(position)

    fun makeMove(
        gameId: GameId,
        position: Position
    ): MoveEvaluation {
        val game = getActiveGame(gameId)
        val result = game.makeMove(position)

        if (result.isValid) {
            gameRepository.update(
                gameId = gameId,
                snapshot = game.snapshot()
            )
        }

        return result
    }

    fun getAvailableMoves(gameId: GameId): Set<Position> =
        getActiveGame(gameId).availableMoves()

    fun getGameSnapshot(gameId: GameId): GameSnapshot =
        getActiveGame(gameId).snapshot()

    fun getGameRecord(gameId: GameId): GameRecord {
        val record = requireNotNull(gameRepository.findById(gameId)) {
            "Game with id ${gameId.value} does not exist"
        }

        return currentGameRecord(record)
    }

    fun getGames(): List<GameRecord> =
        gameRepository.findAll().map(::currentGameRecord)

    private fun currentGameRecord(record: GameRecord): GameRecord {
        val activeGame = activeGames[record.id]

        return if (activeGame == null) {
            record
        } else {
            record.copy(
                snapshot = activeGame.snapshot()
            )
        }
    }

    private fun getActiveGame(gameId: GameId): Game =
        requireNotNull(activeGames[gameId]) {
            "Game with id ${gameId.value} is not active"
        }
}
