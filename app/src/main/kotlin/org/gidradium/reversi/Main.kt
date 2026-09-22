package org.gidradium.reversi

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.repository.InMemoryGameRepository
import org.gidradium.reversi.application.repository.InMemoryPlayerRepository
import org.gidradium.reversi.presentation.cli.Cli
import org.gidradium.reversi.presentation.cli.CliIO

fun main() {
    val playerRepository = InMemoryPlayerRepository()
    val gameRepository = InMemoryGameRepository()

    val adminService = AdminService(
        playerRepository = playerRepository,
        gameRepository = gameRepository
    )

    Cli(
        adminService = adminService,
        io = CliIO()
    ).run()
}
