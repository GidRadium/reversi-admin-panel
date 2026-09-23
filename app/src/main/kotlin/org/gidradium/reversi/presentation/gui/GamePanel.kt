package org.gidradium.reversi.presentation.gui

import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.game.Cell
import org.gidradium.reversi.game.GameStatus
import org.gidradium.reversi.game.Position
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextArea

class GamePanel(
    private val viewModel: GuiViewModel,
    private val onChanged: () -> Unit
) : JPanel(BorderLayout(8, 8)) {

    private val availableCellColor = Color(166, 220, 170)
    private val unavailableCellColor = Color(210, 190, 160)

    private val whitePlayerBox = JComboBox<String>()
    private val blackPlayerBox = JComboBox<String>()

    private val statusLabel = JLabel("No game selected")
    private val scoreLabel = JLabel(" ")
    private val historyArea = JTextArea()

    private val boardButtons = Array(8) { row ->
        Array(8) { column ->
            createBoardButton(row, column)
        }
    }

    init {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        historyArea.isEditable = false
        historyArea.rows = 5

        add(createControls(), BorderLayout.NORTH)
        add(createCenterPanel(), BorderLayout.CENTER)
        add(JScrollPane(historyArea), BorderLayout.SOUTH)
    }

    fun refresh() {
        refreshPlayerBoxes()
        refreshGame()
    }

    private fun createControls(): JPanel {
        val panel = JPanel(GridLayout(2, 3, 8, 8))

        panel.add(JLabel("White"))
        panel.add(JLabel("Black"))
        panel.add(JLabel())

        panel.add(whitePlayerBox)
        panel.add(blackPlayerBox)

        val createGameButton = JButton("Create game")

        createGameButton.addActionListener {
            createGame()
        }

        panel.add(createGameButton)

        return panel
    }

    private fun createCenterPanel(): JPanel {
        val panel = JPanel(BorderLayout(8, 8))

        val information = JPanel(GridLayout(2, 1))
        information.add(statusLabel)
        information.add(scoreLabel)

        val boardPanel = JPanel(GridLayout(8, 8, 2, 2))

        for (row in boardButtons.indices) {
            for (column in boardButtons[row].indices) {
                boardPanel.add(boardButtons[row][column])
            }
        }

        panel.add(information, BorderLayout.NORTH)
        panel.add(boardPanel, BorderLayout.CENTER)

        return panel
    }

    private fun createBoardButton(
        row: Int,
        column: Int
    ): JButton {
        return JButton().apply {
            preferredSize = Dimension(60, 60)

            isFocusPainted = false
            isContentAreaFilled = true
            isBorderPainted = true
            isOpaque = true

            border = BorderFactory.createLineBorder(Color.DARK_GRAY)

            addActionListener {
                try {
                    viewModel.makeMove(Position(row, column))
                    onChanged()
                } catch (exception: IllegalArgumentException) {
                    showError(exception.message)
                }
            }
        }
    }

    private fun refreshPlayerBoxes() {
        val players = viewModel.state.players

        val playerNames = players.map { (playerId, name) ->
            "#${playerId.value}: $name"
        }

        whitePlayerBox.model =
            DefaultComboBoxModel(playerNames.toTypedArray())

        blackPlayerBox.model =
            DefaultComboBoxModel(playerNames.toTypedArray())
    }

    private fun createGame() {
        val whitePlayerId = selectedPlayerId(whitePlayerBox)
        val blackPlayerId = selectedPlayerId(blackPlayerBox)

        if (whitePlayerId == null || blackPlayerId == null) {
            showError("Select both players.")
            return
        }

        try {
            viewModel.createGame(
                whitePlayerId = whitePlayerId,
                blackPlayerId = blackPlayerId
            )

            onChanged()
        } catch (exception: IllegalArgumentException) {
            showError(exception.message)
        }
    }

    private fun refreshGame() {
        val gameId = viewModel.state.selectedGameId

        if (gameId == null) {
            clearGame()
            return
        }

        val game = viewModel.state.games.find {
            it.id == gameId
        }

        if (game == null) {
            clearGame()
            return
        }

        val snapshot = game.snapshot

        statusLabel.text = if (snapshot.status == GameStatus.FINISHED) {
            "Finished: ${snapshot.winner ?: "draw"}"
        } else {
            "Current player: ${snapshot.currentPlayer}"
        }

        val blackPieces = countCells(snapshot.board, Cell.BLACK)
        val whitePieces = countCells(snapshot.board, Cell.WHITE)

        scoreLabel.text =
            "Black: $blackPieces    White: $whitePieces"

        historyArea.text = snapshot.history
            .mapIndexed { index, move ->
                "${index + 1}. " +
                        "${move.player}: " +
                        formatPosition(move.position)
            }
            .joinToString("\n")

        renderBoard(snapshot.board)
    }

    private fun renderBoard(board: List<List<Cell>>) {
        val gameSelected = viewModel.state.selectedGameId != null

        for (row in board.indices) {
            for (column in board[row].indices) {
                val position = Position(row, column)
                val button = boardButtons[row][column]
                val cell = board[row][column]

                button.background = when (cell) {
                    Cell.BLACK -> Color.BLACK
                    Cell.WHITE -> Color.WHITE
                    Cell.EMPTY ->
                        if (position in viewModel.state.availableMoves) {
                            availableCellColor
                        } else {
                            unavailableCellColor
                        }
                }

                button.isEnabled = gameSelected
            }
        }
    }

    private fun clearGame() {
        statusLabel.text = "No game selected"
        scoreLabel.text = " "
        historyArea.text = ""

        for (row in boardButtons.indices) {
            for (column in boardButtons[row].indices) {
                val button = boardButtons[row][column]

                button.background = unavailableCellColor
                button.isEnabled = false
            }
        }
    }

    private fun selectedPlayerId(
        comboBox: JComboBox<String>
    ): PlayerId? {
        val index = comboBox.selectedIndex

        if (index < 0) {
            return null
        }

        return viewModel.state.players.keys.elementAt(index)
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

    private fun showError(message: String?) {
        JOptionPane.showMessageDialog(
            this,
            message ?: "Unknown error",
            "Error",
            JOptionPane.ERROR_MESSAGE
        )
    }
}
