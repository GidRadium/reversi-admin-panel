package org.gidradium.reversi.presentation.cli

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.database.DatabaseFactory
import org.gidradium.reversi.application.repository.ExposedGameRepository
import org.gidradium.reversi.application.repository.ExposedPlayerRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class CliDatabaseSystemTest {

    @TempDir
    lateinit var tempDirectory: Path

    private fun createService(databasePath: Path): AdminService {
        val database = DatabaseFactory.connect(databasePath)
        DatabaseFactory.initialize(database)

        return AdminService(
            playerRepository = ExposedPlayerRepository(database),
            gameRepository = ExposedGameRepository(database)
        )
    }

    @Test
    fun `cli persists players game and move between sessions`() {
        val databasePath = tempDirectory.resolve("test.db")

        val firstCliIO = FakeCliIO(
            mutableListOf(
                "pc Alice",
                "pc Bob",
                "gc 1 2",
                "gm 1 D3",
                "exit"
            )
        )

        Cli(
            adminService = createService(databasePath),
            io = firstCliIO
        ).run()

        assertTrue(
            firstCliIO.outputs.any {
                it.contains("Created player #1: Alice")
            }
        )

        assertTrue(
            firstCliIO.outputs.any {
                it.contains("Created player #2: Bob")
            }
        )

        assertTrue(
            firstCliIO.outputs.any {
                it.contains("Created game #1")
            }
        )

        assertTrue(
            firstCliIO.outputs.any {
                it.contains("Move D3 accepted.")
            }
        )

        val secondCliIO = FakeCliIO(
            mutableListOf(
                "pl",
                "gl",
                "gh 1",
                "gi 1",
                "exit"
            )
        )

        Cli(
            adminService = createService(databasePath),
            io = secondCliIO
        ).run()

        assertTrue(
            secondCliIO.outputs.any {
                it.contains("#1: Alice")
            }
        )

        assertTrue(
            secondCliIO.outputs.any {
                it.contains("#2: Bob")
            }
        )

        assertTrue(
            secondCliIO.outputs.any {
                it.contains("#1: Alice vs Bob")
            }
        )

        assertTrue(
            secondCliIO.outputs.any {
                it.contains("1. BLACK: D3")
            }
        )

        assertTrue(
            secondCliIO.outputs.any {
                it.contains("Moves: 1")
            }
        )
    }
}
