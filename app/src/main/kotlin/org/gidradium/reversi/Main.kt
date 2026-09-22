package org.gidradium.reversi

import org.gidradium.reversi.application.GameService
import org.gidradium.reversi.application.PlayerService
import org.gidradium.reversi.infrastructure.repository.InMemoryGameRepository
import org.gidradium.reversi.infrastructure.repository.InMemoryPlayerRepository
import org.gidradium.reversi.presentation.cli.Cli
import org.gidradium.reversi.presentation.cli.CliIO

fun main() {
    val playerRepository = InMemoryPlayerRepository()
    val gameRepository = InMemoryGameRepository()

    val playerService = PlayerService(
        playerRepository = playerRepository,
        gameRepository = gameRepository
    )

    val gameService = GameService(
        playerRepository = playerRepository,
        gameRepository = gameRepository
    )

    Cli(
        playerService = playerService,
        gameService = gameService,
        io = CliIO()
    ).run()
}
