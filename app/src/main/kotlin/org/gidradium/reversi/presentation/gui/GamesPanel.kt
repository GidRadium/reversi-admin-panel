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

class GamesPanel(
    private val viewModel: GuiViewModel,
    private val onChanged: () -> Unit
) : JPanel(BorderLayout(8, 8)) {

    private val gameListModel = DefaultListModel<String>()
    private val gameList = JList(gameListModel)

    private var refreshing = false

    init {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        preferredSize = Dimension(260, 0)

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
                val index = viewModel.state.games.indexOfFirst {
                    it.id == selectedGameId
                }

                if (index >= 0) {
                    gameList.selectedIndex = index
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

            val index = gameList.selectedIndex

            if (index >= 0) {
                viewModel.selectGame(gameIdAt(index))
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
