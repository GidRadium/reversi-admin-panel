package org.gidradium.reversi.presentation.cli

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.database.DatabaseFactory
import org.gidradium.reversi.application.repository.ExposedGameRepository
import org.gidradium.reversi.application.repository.ExposedPlayerRepository

fun main() {
    val database = DatabaseFactory.connect()
    DatabaseFactory.initialize(database)

    val adminService = AdminService(
        playerRepository = ExposedPlayerRepository(database),
        gameRepository = ExposedGameRepository(database)
    )

    Cli(
        adminService = adminService,
        io = CliIO()
    ).run()
}
