package org.gidradium.reversi.presentation.gui

import org.gidradium.reversi.application.GameId
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel

private const val PANEL_GAP = 8
private const val PANEL_PADDING = 10
private const val PANEL_WIDTH = 260

class GamesPanel(
    private val viewModel: GuiViewModel,
    private val onChanged: () -> Unit
) : JPanel(BorderLayout(PANEL_GAP, PANEL_GAP)) {

    private val gameListModel = DefaultListModel<String>()
    private val gameList = JList(gameListModel)

    private var refreshing = false

    init {
        border = BorderFactory.createEmptyBorder(
            PANEL_PADDING,
            PANEL_PADDING,
            PANEL_PADDING,
            PANEL_PADDING
        )
        preferredSize = Dimension(PANEL_WIDTH, 0)

        createGameList()
        createDeleteButton()

        add(JLabel("Games"), BorderLayout.NORTH)
        add(JScrollPane(gameList), BorderLayout.CENTER)
    }

    fun refresh() {
        refreshing = true

        try {
            val selectedGameId = viewModel.state.selectedGameId

            gameListModel.clear()

            for (game in viewModel.state.games) {
                val whiteName =
                    viewModel.state.players[game.whitePlayerId] ?: "Unknown"

                val blackName =
                    viewModel.state.players[game.blackPlayerId] ?: "Unknown"

                gameListModel.addElement(
                    "#${game.id.value}: $whiteName vs $blackName"
                )
            }

            if (selectedGameId != null) {
                val selectedIndex = viewModel.state.games.indexOfFirst {
                    it.id == selectedGameId
                }

                if (selectedIndex >= 0) {
                    gameList.selectedIndex = selectedIndex
                }
            }
        } finally {
            refreshing = false
        }
    }

    private fun createGameList() {
        gameList.selectionMode = ListSelectionModel.SINGLE_SELECTION

        gameList.addListSelectionListener {
            if (refreshing) {
                return@addListSelectionListener
            }

            val selectedIndex = gameList.selectedIndex

            if (selectedIndex >= 0) {
                viewModel.selectGame(gameIdAt(selectedIndex))
                onChanged()
            }
        }
    }

    private fun createDeleteButton() {
        val deleteButton = JButton("Delete game")

        deleteButton.addActionListener {
            try {
                viewModel.deleteSelectedGame()
                onChanged()
            } catch (exception: IllegalArgumentException) {
                showError(exception.message)
            }
        }

        add(deleteButton, BorderLayout.SOUTH)
    }

    private fun gameIdAt(index: Int): GameId =
        viewModel.state.games[index].id

    private fun showError(message: String?) {
        JOptionPane.showMessageDialog(
            this,
            message ?: "Unknown error",
            "Error",
            JOptionPane.ERROR_MESSAGE
        )
    }
}
