package org.gidradium.reversi.application

import org.gidradium.reversi.application.repository.IGameRepository
import org.gidradium.reversi.application.repository.IPlayerRepository
import org.gidradium.reversi.domain.game.PlayerColor
import org.gidradium.reversi.domain.player.Player
import org.gidradium.reversi.domain.player.PlayerStatistics

class PlayerService(
    private val playerRepository: IPlayerRepository,
    private val gameRepository: IGameRepository
) {

    fun createPlayer(name: String): Player {
        return playerRepository.create(name)
    }

    fun getPlayer(id: Int): Player? {
        return playerRepository.findById(id)
    }

    fun getAllPlayers(): List<Player> {
        return playerRepository.findAll()
    }

    fun deletePlayer(id: Int) {
        requireNotNull(playerRepository.findById(id)) {
            "Player with id $id does not exist"
        }

        val isUsedInGame = gameRepository.findAll().any { game ->
            game.whitePlayerId == id || game.blackPlayerId == id
        }

        require(!isUsedInGame) {
            "Player cannot be deleted because they participate in a game"
        }

        playerRepository.delete(id)
    }

    fun getStatistics(id: Int): PlayerStatistics {
        requireNotNull(playerRepository.findById(id)) {
            "Player with id $id does not exist"
        }

        var gamesPlayed = 0
        var wins = 0
        var losses = 0
        var draws = 0

        for (game in gameRepository.findAll()) {
            val snapshot = game.snapshot
            val color = when (id) {
                game.whitePlayerId -> PlayerColor.WHITE
                game.blackPlayerId -> PlayerColor.BLACK
                else -> null
            }

            if (color == null) {
                continue
            }

            gamesPlayed++

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
}
