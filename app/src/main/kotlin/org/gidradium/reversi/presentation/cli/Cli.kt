package org.gidradium.reversi.presentation.cli

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.GameRecord
import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.game.Cell
import org.gidradium.reversi.game.GameStatus
import org.gidradium.reversi.game.Position

class Cli(
    private val adminService: AdminService,
    private val io: ICliIO
) {

    fun run() {
        io.writeLine("Reversi Admin Panel")
        io.writeLine("Type 'help' to see commands.")

        while (true) {
            io.write("> ")

            val input = io.readLine() ?: return

            try {
                if (!handleCommand(input)) {
                    return
                }
            } catch (exception: IllegalArgumentException) {
                io.writeLine("Error: ${exception.message}")
            }
        }
    }

    private fun handleCommand(input: String): Boolean {
        val parts = input.trim().split(Regex("\\s+"))

        if (parts.isEmpty() || parts[0].isEmpty()) {
            return true
        }

        when (parts[0].lowercase()) {
            "help" -> showHelp()
            "exit" -> return false

            "pc" -> createPlayer(parts)
            "pd" -> deletePlayer(parts)
            "pi" -> showPlayerInfo(parts)
            "pl" -> showPlayers()

            "gc" -> createGame(parts)
            "gd" -> deleteGame(parts)
            "gm" -> makeMove(parts)
            "gi" -> showGameInfo(parts)
            "gb" -> showBoard(parts)
            "gv" -> showVariants(parts)
            "gh" -> showHistory(parts)
            "gl" -> showGames()

            else -> io.writeLine("Unknown command. Type 'help'.")
        }

        return true
    }

    private fun showHelp() {
        io.writeLine("Player commands:")
        io.writeLine("  pc <name>              - player-create")
        io.writeLine("  pd <playerId>          - player-delete")
        io.writeLine("  pi <playerId>          - player-info")
        io.writeLine("  pl                     - players-list")

        io.writeLine("Game commands:")
        io.writeLine("  gc <whiteId> <blackId> - game-create")
        io.writeLine("  gd <gameId>            - game-delete")
        io.writeLine("  gm <gameId> <position> - game-move")
        io.writeLine("  gi <gameId>            - game-info")
        io.writeLine("  gb <gameId>            - game-board")
        io.writeLine("  gv <gameId>            - game-variants")
        io.writeLine("  gh <gameId>            - game-history")
        io.writeLine("  gl                     - games-list")

        io.writeLine("  exit                   - exit")
    }

    private fun createPlayer(parts: List<String>) {
        require(parts.size >= 2) {
            "Usage: pc <name>"
        }

        val name = parts.drop(1).joinToString(" ")
        val playerId = adminService.createPlayer(name)

        io.writeLine("Created player #${playerId.value}: $name")
    }

    private fun deletePlayer(parts: List<String>) {
        require(parts.size == 2) {
            "Usage: pd <playerId>"
        }

        val playerId = PlayerId(parts[1].toInt())

        adminService.deletePlayer(playerId)

        io.writeLine("Deleted player #${playerId.value}")
    }

    private fun showPlayerInfo(parts: List<String>) {
        require(parts.size == 2) {
            "Usage: pi <playerId>"
        }

        val playerId = PlayerId(parts[1].toInt())

        val playerName = requireNotNull(adminService.getPlayer(playerId)) {
            "Player with id ${playerId.value} does not exist"
        }

        val statistics = adminService.getPlayerStatistics(playerId)

        io.writeLine("Player #${playerId.value}: $playerName")
        io.writeLine("Games: ${statistics.gamesPlayed}")
        io.writeLine("Wins: ${statistics.wins}")
        io.writeLine("Losses: ${statistics.losses}")
        io.writeLine("Draws: ${statistics.draws}")
    }

    private fun showPlayers() {
        val players = adminService.getPlayers()

        if (players.isEmpty()) {
            io.writeLine("No players.")
            return
        }

        for ((playerId, name) in players) {
            io.writeLine("#${playerId.value}: $name")
        }
    }

    private fun createGame(parts: List<String>) {
        require(parts.size == 3) {
            "Usage: gc <whitePlayerId> <blackPlayerId>"
        }

        val whitePlayerId = PlayerId(parts[1].toInt())
        val blackPlayerId = PlayerId(parts[2].toInt())

        val gameId = adminService.createGame(
            whitePlayerId = whitePlayerId,
            blackPlayerId = blackPlayerId
        )

        io.writeLine("Created game #${gameId.value}")
    }

    private fun deleteGame(parts: List<String>) {
        require(parts.size == 2) {
            "Usage: gd <gameId>"
        }

        val gameId = GameId(parts[1].toInt())

        adminService.deleteGame(gameId)

        io.writeLine("Deleted game #${gameId.value}")
    }

    private fun makeMove(parts: List<String>) {
        require(parts.size == 3) {
            "Usage: gm <gameId> <position>"
        }

        val gameId = GameId(parts[1].toInt())
        val position = PositionParser.parse(parts[2])

        val result = adminService.makeMove(
            gameId = gameId,
            position = position
        )

        if (result.isValid) {
            io.writeLine("Move ${formatPosition(position)} accepted.")
        } else {
            io.writeLine("Move rejected: ${result.reason}")
        }
    }

    private fun showGameInfo(parts: List<String>) {
        require(parts.size == 2) {
            "Usage: gi <gameId>"
        }

        val gameId = GameId(parts[1].toInt())
        val game = adminService.getGameRecord(gameId)

        showGameHeader(game)

        val board = game.snapshot.board

        io.writeLine("Black pieces: ${countCells(board, Cell.BLACK)}")
        io.writeLine("White pieces: ${countCells(board, Cell.WHITE)}")
        io.writeLine("Empty cells: ${countCells(board, Cell.EMPTY)}")
        io.writeLine("Moves: ${game.snapshot.history.size}")
    }

    private fun showBoard(parts: List<String>) {
        require(parts.size == 2) {
            "Usage: gb <gameId>"
        }

        val gameId = GameId(parts[1].toInt())
        val game = adminService.getGameRecord(gameId)

        printBoard(game.snapshot.board)
    }

    private fun showVariants(parts: List<String>) {
        require(parts.size == 2) {
            "Usage: gv <gameId>"
        }

        val gameId = GameId(parts[1].toInt())
        val game = adminService.getGameRecord(gameId)
        val availableMoves = adminService.getAvailableMoves(gameId)

        printBoard(
            board = game.snapshot.board,
            highlightedCells = availableMoves
        )
    }

    private fun showHistory(parts: List<String>) {
        require(parts.size == 2) {
            "Usage: gh <gameId>"
        }

        val gameId = GameId(parts[1].toInt())
        val game = adminService.getGameRecord(gameId)

        if (game.snapshot.history.isEmpty()) {
            io.writeLine("No moves.")
            return
        }

        for ((index, move) in game.snapshot.history.withIndex()) {
            io.writeLine(
                "${index + 1}. " +
                        "${move.player}: ${formatPosition(move.position)}"
            )
        }
    }

    private fun showGames() {
        val games = adminService.getGames()

        if (games.isEmpty()) {
            io.writeLine("No games.")
            return
        }

        for (game in games) {
            val whiteName = adminService.getPlayer(game.whitePlayerId)
            val blackName = adminService.getPlayer(game.blackPlayerId)

            io.writeLine(
                "#${game.id.value}: " +
                        "${whiteName ?: "Unknown"} vs " +
                        "${blackName ?: "Unknown"}"
            )
        }
    }

    private fun showGameHeader(game: GameRecord) {
        val whiteName = adminService.getPlayer(game.whitePlayerId)
        val blackName = adminService.getPlayer(game.blackPlayerId)

        io.writeLine("Game #${game.id.value}")
        io.writeLine(
            "White: #${game.whitePlayerId.value} " +
                    "${whiteName ?: "Unknown"}"
        )
        io.writeLine(
            "Black: #${game.blackPlayerId.value} " +
                    "${blackName ?: "Unknown"}"
        )
        io.writeLine("Status: ${game.snapshot.status}")
        io.writeLine("Current player: ${game.snapshot.currentPlayer}")

        if (game.snapshot.status == GameStatus.FINISHED) {
            io.writeLine("Winner: ${game.snapshot.winner ?: "draw"}")
        }
    }

    private fun printBoard(
        board: List<List<Cell>>,
        highlightedCells: Set<Position> = emptySet()
    ) {
        io.writeLine("  A B C D E F G H")

        for (row in board.indices) {
            val line = buildString {
                append(row + 1)
                append(' ')

                for (column in board[row].indices) {
                    val position = Position(row, column)

                    val symbol = when {
                        board[row][column] == Cell.BLACK -> 'B'
                        board[row][column] == Cell.WHITE -> 'W'
                        position in highlightedCells -> '+'
                        else -> '.'
                    }

                    append(symbol)
                    append(' ')
                }
            }

            io.writeLine(line)
        }
    }

    private fun countCells(
        board: List<List<Cell>>,
        cell: Cell
    ): Int =
        board.sumOf { row ->
            row.count { it == cell }
        }

    private fun formatPosition(position: Position): String =
        "${('A'.code + position.column).toChar()}${position.row + 1}"
}
