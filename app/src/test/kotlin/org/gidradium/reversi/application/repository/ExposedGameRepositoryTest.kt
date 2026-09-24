package org.gidradium.reversi.application.repository

import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.application.database.DatabaseFactory
import org.gidradium.reversi.application.database.MovesTable
import org.gidradium.reversi.game.Game
import org.gidradium.reversi.game.GameStatus
import org.gidradium.reversi.game.PlayerColor
import org.gidradium.reversi.game.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.nio.file.Path

class ExposedGameRepositoryTest {

    @TempDir
    lateinit var tempDirectory: Path

    private fun createRepository(): ExposedGameRepository {
        val database = DatabaseFactory.connect(
            tempDirectory.resolve("test.db")
        )

        DatabaseFactory.initialize(database)

        val playerRepository = ExposedPlayerRepository(database)

        playerRepository.create("Alice")
        playerRepository.create("Bob")
        playerRepository.create("Carol")
        playerRepository.create("Dave")

        return ExposedGameRepository(database)
    }

    @Test
    fun `create stores game`() {
        val repository = createRepository()

        val snapshot = Game().snapshot()

        val gameId = repository.create(
            whitePlayerId = PlayerId(1),
            blackPlayerId = PlayerId(2),
            snapshot = snapshot
        )

        val game = repository.findById(gameId)

        assertNotNull(game)
        assertEquals(gameId, game!!.id)
        assertEquals(PlayerId(1), game.whitePlayerId)
        assertEquals(PlayerId(2), game.blackPlayerId)
        assertEquals(snapshot, game.snapshot)
    }

    @Test
    fun `update stores new snapshot and history`() {
        val repository = createRepository()

        val game = Game()

        val gameId = repository.create(
            whitePlayerId = PlayerId(1),
            blackPlayerId = PlayerId(2),
            snapshot = game.snapshot()
        )

        game.makeMove(Position(2, 3))
        game.makeMove(Position(2, 2))

        val updatedSnapshot = game.snapshot()

        repository.update(
            gameId = gameId,
            snapshot = updatedSnapshot
        )

        val storedGame = repository.findById(gameId)

        assertNotNull(storedGame)
        assertEquals(
            updatedSnapshot,
            storedGame!!.snapshot
        )
    }

    @Test
    fun `history survives repository recreation`() {
        val databasePath = tempDirectory.resolve("test.db")

        val firstDatabase = DatabaseFactory.connect(databasePath)
        DatabaseFactory.initialize(firstDatabase)

        val playerRepository = ExposedPlayerRepository(firstDatabase)

        playerRepository.create("Alice")
        playerRepository.create("Bob")

        val firstRepository = ExposedGameRepository(firstDatabase)

        val game = Game()
        game.makeMove(Position(2, 3))
        game.makeMove(Position(2, 2))

        val snapshot = game.snapshot()

        val gameId = firstRepository.create(
            whitePlayerId = PlayerId(1),
            blackPlayerId = PlayerId(2),
            snapshot = snapshot
        )

        val secondDatabase = DatabaseFactory.connect(databasePath)
        val secondRepository = ExposedGameRepository(secondDatabase)

        val storedGame = secondRepository.findById(gameId)

        assertNotNull(storedGame)
        assertEquals(
            snapshot.history,
            storedGame!!.snapshot.history
        )
        assertEquals(
            snapshot.board,
            storedGame.snapshot.board
        )
    }

    @Test
    fun `find all returns all games`() {
        val repository = createRepository()

        val firstGame = repository.create(
            whitePlayerId = PlayerId(1),
            blackPlayerId = PlayerId(2),
            snapshot = Game().snapshot()
        )

        val secondGame = repository.create(
            whitePlayerId = PlayerId(3),
            blackPlayerId = PlayerId(4),
            snapshot = Game().snapshot()
        )

        assertEquals(
            listOf(firstGame, secondGame),
            repository.findAll().map { it.id }
        )
    }

    @Test
    fun `delete removes game`() {
        val repository = createRepository()

        val gameId = repository.create(
            whitePlayerId = PlayerId(1),
            blackPlayerId = PlayerId(2),
            snapshot = Game().snapshot()
        )

        repository.delete(gameId)

        assertNull(repository.findById(gameId))
    }

    @Test
    fun `delete unknown game is rejected`() {
        val repository = createRepository()

        assertThrows<IllegalArgumentException> {
            repository.delete(
                org.gidradium.reversi.application.GameId(999)
            )
        }
    }

    @Test
    fun `finished game is restored correctly`() {
        val repository = createRepository()

        val game = Game()

        val initialSnapshot = game.snapshot().copy(
            status = GameStatus.FINISHED,
            winner = PlayerColor.WHITE
        )

        val gameId = repository.create(
            whitePlayerId = PlayerId(1),
            blackPlayerId = PlayerId(2),
            snapshot = initialSnapshot
        )

        val storedGame = repository.findById(gameId)

        assertNotNull(storedGame)
        assertEquals(
            GameStatus.FINISHED,
            storedGame!!.snapshot.status
        )
        assertEquals(
            PlayerColor.WHITE,
            storedGame.snapshot.winner
        )
    }

    @Test
    fun `delete game also deletes its moves`() {
        val databasePath = tempDirectory.resolve("cascade.db")
        val database = DatabaseFactory.connect(databasePath)
        DatabaseFactory.initialize(database)

        val playerRepository = ExposedPlayerRepository(database)

        playerRepository.create("Alice")
        playerRepository.create("Bob")

        val repository = ExposedGameRepository(database)

        val game = Game()
        game.makeMove(Position(2, 3))

        val gameId = repository.create(
            whitePlayerId = PlayerId(1),
            blackPlayerId = PlayerId(2),
            snapshot = game.snapshot()
        )

        transaction(database) {
            assertEquals(
                1L,
                MovesTable
                    .selectAll()
                    .count()
            )
        }

        repository.delete(gameId)

        transaction(database) {
            assertEquals(
                0L,
                MovesTable
                    .selectAll()
                    .count()
            )
        }
    }
}
