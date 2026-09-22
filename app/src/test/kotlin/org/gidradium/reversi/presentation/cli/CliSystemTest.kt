package org.gidradium.reversi.presentation.cli

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.infrastructure.memory.InMemoryGameRepository
import org.gidradium.reversi.infrastructure.memory.InMemoryPlayerRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CliSystemTest {

    @Test
    fun `user can create players game and make move`() {
        val playerRepository = InMemoryPlayerRepository()
        val gameRepository = InMemoryGameRepository()

        val service = AdminService(
            playerRepository = playerRepository,
            gameRepository = gameRepository
        )

        val io = FakeCliIO(
            mutableListOf(
                "pc Alice",
                "pc Bob",
                "gc 1 2",
                "gm 1 D3",
                "gi 1",
                "exit"
            )
        )

        Cli(
            adminService = service,
            io = io
        ).run()

        assertTrue(
            io.outputs.any {
                it.contains("Created player #1: Alice")
            }
        )

        assertTrue(
            io.outputs.any {
                it.contains("Created player #2: Bob")
            }
        )

        assertTrue(
            io.outputs.any {
                it.contains("Created game #1")
            }
        )

        assertTrue(
            io.outputs.any {
                it.contains("Move D3 accepted.")
            }
        )
    }
}
