package org.gidradium.reversi.game

import kotlin.collections.plusAssign

class Game(
    private val rules: ReversiRules = ReversiRules()
) {

    private val board = Board()
    private val history = mutableListOf<Move>()

    private var currentPlayer = PlayerColor.BLACK
    private var status = GameStatus.IN_PROGRESS
    private var winner: PlayerColor? = null

    fun validateMove(position: Position): MoveEvaluation {
        if (status == GameStatus.FINISHED) {
            return MoveEvaluation(
                isValid = false,
                reason = "Game is already finished"
            )
        }

        return rules.evaluateMove(
            board = board,
            player = currentPlayer,
            position = position
        )
    }

    fun availableMoves(): Set<Position> {
        if (status == GameStatus.FINISHED) {
            return emptySet()
        }

        return rules.availableMoves(
            board = board,
            player = currentPlayer
        )
    }

    fun makeMove(position: Position): MoveEvaluation {
        val evaluation = validateMove(position)

        if (!evaluation.isValid) {
            return evaluation
        }

        val playerWhoMoved = currentPlayer

        board[position] = playerWhoMoved.toCell()

        for (flippedCell in evaluation.flippedCells) {
            board[flippedCell] = playerWhoMoved.toCell()
        }

        history += Move(
            position = position,
            player = playerWhoMoved
        )

        val progress = rules.determineGameProgress(
            board = board,
            playerWhoMoved = playerWhoMoved
        )

        currentPlayer = progress.currentPlayer
        status = progress.status
        winner = progress.winner

        return evaluation
    }

    fun snapshot(): GameSnapshot {
        return GameSnapshot(
            board = board.snapshot(),
            currentPlayer = currentPlayer,
            history = history.toList(),
            status = status,
            winner = winner
        )
    }

    private fun PlayerColor.toCell(): Cell {
        return when (this) {
            PlayerColor.BLACK -> Cell.BLACK
            PlayerColor.WHITE -> Cell.WHITE
        }
    }
}
