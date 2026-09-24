package org.gidradium.reversi.application.database

import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class DatabaseFactoryTest {

    @TempDir
    lateinit var tempDirectory: Path

    @Test
    fun `initialize creates database schema`() {
        val databasePath = tempDirectory.resolve("test.db")
        val database = DatabaseFactory.connect(databasePath)

        DatabaseFactory.initialize(database)

        transaction(database) {
            PlayersTable.insert {
                it[name] = "Alice"
            }

            assertEquals(
                listOf("Alice"),
                PlayersTable
                    .selectAll()
                    .map { it[PlayersTable.name] }
            )

            assertEquals(
                0L,
                GamesTable.selectAll().count()
            )

            assertEquals(
                0L,
                MovesTable.selectAll().count()
            )
        }
    }
}
