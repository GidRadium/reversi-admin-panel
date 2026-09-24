package org.gidradium.reversi.application.repository

import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.application.database.DatabaseFactory
import org.gidradium.reversi.application.database.MovesTable
import org.gidradium.reversi.game.Game
import org.gidradium.reversi.game.Position
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class ExposedPlayerRepositoryTest {

    @TempDir
    lateinit var tempDirectory: Path

    private fun createRepository(): ExposedPlayerRepository {
        val database = DatabaseFactory.connect(
            tempDirectory.resolve("test.db")
        )

        DatabaseFactory.initialize(database)

        return ExposedPlayerRepository(database)
    }

    @Test
    fun `create stores player`() {
        val repository = createRepository()

        val playerId = repository.create("Alice")

        assertEquals("Alice", repository.findById(playerId))
    }

    @Test
    fun `find all returns all players`() {
        val repository = createRepository()

        val alice = repository.create("Alice")
        val bob = repository.create("Bob")

        assertEquals(
            mapOf(
                alice to "Alice",
                bob to "Bob"
            ),
            repository.findAll()
        )
    }

    @Test
    fun `find by id returns null for unknown player`() {
        val repository = createRepository()

        assertNull(repository.findById(PlayerId(999)))
    }

    @Test
    fun `delete removes player`() {
        val repository = createRepository()

        val playerId = repository.create("Alice")

        repository.delete(playerId)

        assertNull(repository.findById(playerId))
    }

    @Test
    fun `delete unknown player is rejected`() {
        val repository = createRepository()

        assertThrows<IllegalArgumentException> {
            repository.delete(PlayerId(999))
        }
    }

    @Test
    fun `create rejects blank player name`() {
        val repository = createRepository()

        assertThrows<IllegalArgumentException> {
            repository.create("   ")
        }
    }

    @Test
    fun `player remains stored after repository is recreated`() {
        val databasePath = tempDirectory.resolve("test.db")

        val firstDatabase = DatabaseFactory.connect(databasePath)
        DatabaseFactory.initialize(firstDatabase)

        val firstRepository = ExposedPlayerRepository(firstDatabase)
        val playerId = firstRepository.create("Alice")

        val secondDatabase = DatabaseFactory.connect(databasePath)
        val secondRepository = ExposedPlayerRepository(secondDatabase)

        assertEquals(
            "Alice",
            secondRepository.findById(playerId)
        )
    }
}
