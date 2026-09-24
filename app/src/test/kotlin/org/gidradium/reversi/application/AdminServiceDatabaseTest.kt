package org.gidradium.reversi.application

import org.gidradium.reversi.application.database.DatabaseFactory
import org.gidradium.reversi.application.repository.ExposedGameRepository
import org.gidradium.reversi.application.repository.ExposedPlayerRepository
import org.gidradium.reversi.game.PlayerColor
import org.gidradium.reversi.game.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class AdminServiceDatabaseTest {

    @TempDir
    lateinit var tempDirectory: Path

    @Test
    fun `service restores game after being recreated`() {
        val databasePath = tempDirectory.resolve("test.db")

        val firstDatabase = DatabaseFactory.connect(databasePath)
        DatabaseFactory.initialize(firstDatabase)

        val firstPlayerRepository = ExposedPlayerRepository(firstDatabase)
        val firstGameRepository = ExposedGameRepository(firstDatabase)

        val firstService = AdminService(
            playerRepository = firstPlayerRepository,
            gameRepository = firstGameRepository
        )

        val whitePlayer = firstService.createPlayer("Alice")
        val blackPlayer = firstService.createPlayer("Bob")

        val gameId = firstService.createGame(
            whitePlayerId = whitePlayer,
            blackPlayerId = blackPlayer
        )

        val moveResult = firstService.makeMove(
            gameId = gameId,
            position = Position(2, 3)
        )

        assertTrue(moveResult.isValid)

        val secondDatabase = DatabaseFactory.connect(databasePath)

        val secondService = AdminService(
            playerRepository = ExposedPlayerRepository(secondDatabase),
            gameRepository = ExposedGameRepository(secondDatabase)
        )

        val restoredSnapshot = secondService.getGameSnapshot(gameId)

        assertEquals(1, restoredSnapshot.history.size)
        assertEquals(
            PlayerColor.BLACK,
            restoredSnapshot.history.first().player
        )
        assertEquals(
            Position(2, 3),
            restoredSnapshot.history.first().position
        )
        assertEquals(
            PlayerColor.WHITE,
            restoredSnapshot.currentPlayer
        )

        assertEquals(
            firstService.getAvailableMoves(gameId),
            secondService.getAvailableMoves(gameId)
        )
    }

    @Test
    fun `player statistics remain correct after service recreation`() {
        val databasePath = tempDirectory.resolve("statistics.db")

        val firstDatabase = DatabaseFactory.connect(databasePath)
        DatabaseFactory.initialize(firstDatabase)

        val firstService = AdminService(
            playerRepository = ExposedPlayerRepository(firstDatabase),
            gameRepository = ExposedGameRepository(firstDatabase)
        )

        val whitePlayer = firstService.createPlayer("Alice")
        val blackPlayer = firstService.createPlayer("Bob")

        val gameId = firstService.createGame(
            whitePlayerId = whitePlayer,
            blackPlayerId = blackPlayer
        )

        firstService.makeMove(
            gameId = gameId,
            position = Position(2, 3)
        )

        val secondDatabase = DatabaseFactory.connect(databasePath)

        val secondService = AdminService(
            playerRepository = ExposedPlayerRepository(secondDatabase),
            gameRepository = ExposedGameRepository(secondDatabase)
        )

        assertEquals(
            PlayerStatistics(
                gamesPlayed = 1,
                wins = 0,
                losses = 0,
                draws = 0
            ),
            secondService.getPlayerStatistics(whitePlayer)
        )

        assertEquals(
            PlayerStatistics(
                gamesPlayed = 1,
                wins = 0,
                losses = 0,
                draws = 0
            ),
            secondService.getPlayerStatistics(blackPlayer)
        )
    }
}
