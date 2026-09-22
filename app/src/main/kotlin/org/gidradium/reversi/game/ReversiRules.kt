package org.gidradium.reversi.game

class ReversiRules {

    fun evaluateMove(
        board: Board,
        player: PlayerColor,
        position: Position
    ): MoveEvaluation {
        if (board[position] != Cell.EMPTY) {
            return MoveEvaluation(
                isValid = false,
                reason = "Cell is not empty"
            )
        }

        val playerCell = player.toCell()
        val opponentCell = player.opponent().toCell()
        val flippedCells = mutableListOf<Position>()

        for ((rowDelta, columnDelta) in directions) {
            flippedCells += findFlipsInDirection(
                board = board,
                position = position,
                playerCell = playerCell,
                opponentCell = opponentCell,
                rowDelta = rowDelta,
                columnDelta = columnDelta
            )
        }

        if (flippedCells.isEmpty()) {
            return MoveEvaluation(
                isValid = false,
                reason = "Move does not capture any opponent pieces"
            )
        }

        return MoveEvaluation(
            isValid = true,
            flippedCells = flippedCells
        )
    }

    fun availableMoves(
        board: Board,
        player: PlayerColor
    ): Set<Position> {
        val result = mutableSetOf<Position>()

        for (row in 0 until Board.SIZE) {
            for (column in 0 until Board.SIZE) {
                val position = Position(row, column)

                if (evaluateMove(board, player, position).isValid) {
                    result += position
                }
            }
        }

        return result
    }

    fun determineGameProgress(
        board: Board,
        playerWhoMoved: PlayerColor
    ): GameProgress {
        val nextPlayer = playerWhoMoved.opponent()

        if (availableMoves(board, nextPlayer).isNotEmpty()) {
            return GameProgress(
                currentPlayer = nextPlayer,
                status = GameStatus.IN_PROGRESS,
                winner = null
            )
        }

        if (availableMoves(board, playerWhoMoved).isNotEmpty()) {
            return GameProgress(
                currentPlayer = playerWhoMoved,
                status = GameStatus.IN_PROGRESS,
                winner = null
            )
        }

        return GameProgress(
            currentPlayer = playerWhoMoved,
            status = GameStatus.FINISHED,
            winner = determineWinner(board)
        )
    }

    private fun determineWinner(board: Board): PlayerColor? {
        val blackCount = board.count(Cell.BLACK)
        val whiteCount = board.count(Cell.WHITE)

        return when {
            blackCount > whiteCount -> PlayerColor.BLACK
            whiteCount > blackCount -> PlayerColor.WHITE
            else -> null
        }
    }

    private fun findFlipsInDirection(
        board: Board,
        position: Position,
        playerCell: Cell,
        opponentCell: Cell,
        rowDelta: Int,
        columnDelta: Int
    ): List<Position> {
        val result = mutableListOf<Position>()

        var row = position.row + rowDelta
        var column = position.column + columnDelta

        while (
            row in 0 until Board.SIZE &&
            column in 0 until Board.SIZE &&
            board[Position(row, column)] == opponentCell
        ) {
            result += Position(row, column)

            row += rowDelta
            column += columnDelta
        }

        if (result.isEmpty()) {
            return emptyList()
        }

        if (
            row !in 0 until Board.SIZE ||
            column !in 0 until Board.SIZE
        ) {
            return emptyList()
        }

        return if (board[Position(row, column)] == playerCell) {
            result
        } else {
            emptyList()
        }
    }

    private fun PlayerColor.toCell(): Cell =
        when (this) {
            PlayerColor.BLACK -> Cell.BLACK
            PlayerColor.WHITE -> Cell.WHITE
        }

    private fun PlayerColor.opponent(): PlayerColor =
        when (this) {
            PlayerColor.BLACK -> PlayerColor.WHITE
            PlayerColor.WHITE -> PlayerColor.BLACK
        }

    companion object {
        private val directions = listOf(
            -1 to -1,
            -1 to 0,
            -1 to 1,
            0 to -1,
            0 to 1,
            1 to -1,
            1 to 0,
            1 to 1
        )
    }
}
