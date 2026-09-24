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

private const val BOARD_SIZE = 8
private const val CELL_SIZE = 60

private const val PANEL_GAP = 8
private const val PANEL_PADDING = 10
private const val BOARD_GAP = 2
private const val HISTORY_ROWS = 5

private val AVAILABLE_CELL_COLOR = Color(166, 220, 170)
private val UNAVAILABLE_CELL_COLOR = Color(210, 190, 160)
private val CELL_BORDER_COLOR = Color.DARK_GRAY

class GamePanel(
    private val viewModel: GuiViewModel,
    private val onChanged: () -> Unit
) : JPanel(BorderLayout(PANEL_GAP, PANEL_GAP)) {

    private val whitePlayerBox = JComboBox<String>()
    private val blackPlayerBox = JComboBox<String>()

    private val statusLabel = JLabel("No game selected")
    private val scoreLabel = JLabel(" ")
    private val historyArea = JTextArea()

    private val boardButtons = Array(BOARD_SIZE) { row ->
        Array(BOARD_SIZE) { column ->
            createBoardButton(row, column)
        }
    }

    init {
        border = BorderFactory.createEmptyBorder(
            PANEL_PADDING,
            PANEL_PADDING,
            PANEL_PADDING,
            PANEL_PADDING
        )

        historyArea.isEditable = false
        historyArea.rows = HISTORY_ROWS

        add(createControls(), BorderLayout.NORTH)
        add(createCenterPanel(), BorderLayout.CENTER)
        add(JScrollPane(historyArea), BorderLayout.SOUTH)
    }

    fun refresh() {
        refreshPlayerBoxes()
        refreshGame()
    }

    private fun createControls(): JPanel {
        val panel = JPanel(
            GridLayout(2, 3, PANEL_GAP, PANEL_GAP)
        )

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
        val panel = JPanel(
            BorderLayout(PANEL_GAP, PANEL_GAP)
        )

        val informationPanel = JPanel(
            GridLayout(2, 1)
        )

        informationPanel.add(statusLabel)
        informationPanel.add(scoreLabel)

        val boardPanel = JPanel(
            GridLayout(BOARD_SIZE, BOARD_SIZE, BOARD_GAP, BOARD_GAP)
        )

        for (row in boardButtons.indices) {
            for (column in boardButtons[row].indices) {
                boardPanel.add(boardButtons[row][column])
            }
        }

        panel.add(informationPanel, BorderLayout.NORTH)
        panel.add(boardPanel, BorderLayout.CENTER)

        return panel
    }

    private fun createBoardButton(
        row: Int,
        column: Int
    ): JButton =
        JButton().apply {
            preferredSize = Dimension(CELL_SIZE, CELL_SIZE)

            isFocusPainted = false
            isContentAreaFilled = true
            isBorderPainted = true
            isOpaque = true

            border = BorderFactory.createLineBorder(CELL_BORDER_COLOR)

            addActionListener {
                try {
                    viewModel.makeMove(Position(row, column))
                    onChanged()
                } catch (exception: IllegalArgumentException) {
                    showError(exception.message)
                }
            }
        }

    private fun refreshPlayerBoxes() {
        val playerNames = viewModel.state.players
            .map { (playerId, name) ->
                "#${playerId.value}: $name"
            }
            .toTypedArray()

        whitePlayerBox.model = DefaultComboBoxModel(playerNames)
        blackPlayerBox.model = DefaultComboBoxModel(playerNames)
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

        scoreLabel.text = "Black: $blackPieces    White: $whitePieces"

        historyArea.text = snapshot.history
            .mapIndexed { index, move ->
                "${index + 1}. ${move.player}: ${formatPosition(move.position)}"
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

                button.background = when (board[row][column]) {
                    Cell.BLACK -> Color.BLACK
                    Cell.WHITE -> Color.WHITE
                    Cell.EMPTY -> getEmptyCellColor(position)
                }

                button.isEnabled = gameSelected
            }
        }
    }

    private fun getEmptyCellColor(position: Position): Color =
        if (position in viewModel.state.availableMoves) {
            AVAILABLE_CELL_COLOR
        } else {
            UNAVAILABLE_CELL_COLOR
        }

    private fun clearGame() {
        statusLabel.text = "No game selected"
        scoreLabel.text = " "
        historyArea.text = ""

        for (row in boardButtons.indices) {
            for (column in boardButtons[row].indices) {
                val button = boardButtons[row][column]

                button.background = UNAVAILABLE_CELL_COLOR
                button.isEnabled = false
            }
        }
    }

    private fun selectedPlayerId(
        comboBox: JComboBox<String>
    ): PlayerId? {
        val selectedIndex = comboBox.selectedIndex

        if (selectedIndex < 0) {
            return null
        }

        return viewModel.state.players.keys.elementAt(selectedIndex)
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
