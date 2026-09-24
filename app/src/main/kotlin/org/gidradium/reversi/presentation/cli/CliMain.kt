package org.gidradium.reversi.presentation.cli

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.repository.InMemoryGameRepository
import org.gidradium.reversi.application.repository.InMemoryPlayerRepository

fun main() {
    val adminService = AdminService(
        playerRepository = InMemoryPlayerRepository(),
        gameRepository = InMemoryGameRepository()
    )

    Cli(
        adminService = adminService,
        io = CliIO()
    ).run()
}
