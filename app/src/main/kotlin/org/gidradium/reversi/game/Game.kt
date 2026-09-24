package org.gidradium.reversi.game

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
        val playerCell = playerWhoMoved.toCell()

        board[position] = playerCell

        for (flippedCell in evaluation.flippedCells) {
            board[flippedCell] = playerCell
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

    fun snapshot(): GameSnapshot =
        GameSnapshot(
            board = board.snapshot(),
            currentPlayer = currentPlayer,
            history = history.toList(),
            status = status,
            winner = winner
        )

    private fun PlayerColor.toCell(): Cell =
        when (this) {
            PlayerColor.BLACK -> Cell.BLACK
            PlayerColor.WHITE -> Cell.WHITE
        }

    companion object {
        fun fromSnapshot(
            snapshot: GameSnapshot,
            rules: ReversiRules = ReversiRules()
        ): Game {
            val game = Game(rules)

            game.board.restore(snapshot.board)
            game.history.addAll(snapshot.history)
            game.currentPlayer = snapshot.currentPlayer
            game.status = snapshot.status
            game.winner = snapshot.winner

            return game
        }
    }
}
