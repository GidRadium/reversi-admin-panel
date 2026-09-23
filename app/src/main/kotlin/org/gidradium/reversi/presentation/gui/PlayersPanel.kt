package org.gidradium.reversi.presentation.gui

import org.gidradium.reversi.application.PlayerId
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.BorderFactory
import javax.swing.DefaultListModel
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.ListSelectionModel

class PlayersPanel(
    private val viewModel: GuiViewModel,
    private val onChanged: () -> Unit
) : JPanel(BorderLayout(8, 8)) {

    private val playerListModel = DefaultListModel<String>()
    private val playerList = JList(playerListModel)

    private val nameField = JTextField()
    private val statisticsLabel = JLabel("No player selected")

    private var refreshing = false

    init {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        preferredSize = Dimension(260, 0)

        createPlayerList()
        createButtons()

        add(JLabel("Players"), BorderLayout.NORTH)
        add(JScrollPane(playerList), BorderLayout.CENTER)
    }

    fun refresh() {
        refreshing = true

        try {
            val selectedPlayerId = viewModel.state.selectedPlayerId

            playerListModel.clear()

            for ((playerId, name) in viewModel.state.players) {
                playerListModel.addElement(
                    "#${playerId.value}: $name"
                )
            }

            if (selectedPlayerId != null) {
                val index = viewModel.state.players.keys.indexOf(selectedPlayerId)

                if (index >= 0) {
                    playerList.selectedIndex = index
                }
            }

            refreshStatistics()
        } finally {
            refreshing = false
        }
    }

    private fun createPlayerList() {
        playerList.selectionMode = ListSelectionModel.SINGLE_SELECTION

        playerList.addListSelectionListener {
            if (refreshing) {
                return@addListSelectionListener
            }

            val index = playerList.selectedIndex

            if (index >= 0) {
                val playerId = playerIdAt(index)

                viewModel.selectPlayer(playerId)
                refreshStatistics()
            }
        }
    }

    private fun createButtons() {
        val createButton = JButton("Create")

        createButton.addActionListener {
            createPlayer()
        }

        val deleteButton = JButton("Delete")

        deleteButton.addActionListener {
            deletePlayer()
        }

        val buttons = JPanel(GridLayout(1, 2, 8, 8))
        buttons.add(createButton)
        buttons.add(deleteButton)

        val bottomPanel = JPanel(BorderLayout(8, 8))
        bottomPanel.add(nameField, BorderLayout.CENTER)
        bottomPanel.add(buttons, BorderLayout.SOUTH)
        bottomPanel.add(statisticsLabel, BorderLayout.NORTH)

        add(bottomPanel, BorderLayout.SOUTH)
    }

    private fun createPlayer() {
        try {
            val name = nameField.text.trim()

            viewModel.createPlayer(name)

            nameField.text = ""

            onChanged()
        } catch (exception: IllegalArgumentException) {
            showError(exception.message)
        }
    }

    private fun deletePlayer() {
        try {
            viewModel.deleteSelectedPlayer()
            onChanged()
        } catch (exception: IllegalArgumentException) {
            showError(exception.message)
        }
    }

    private fun refreshStatistics() {
        val statistics = viewModel.state.selectedPlayerStatistics

        statisticsLabel.text = if (statistics == null) {
            "No player selected"
        } else {
            """
            <html>
            Games: ${statistics.gamesPlayed}<br>
            Wins: ${statistics.wins}<br>
            Losses: ${statistics.losses}<br>
            Draws: ${statistics.draws}
            </html>
            """.trimIndent()
        }
    }

    private fun playerIdAt(index: Int): PlayerId =
        viewModel.state.players.keys.elementAt(index)

    private fun showError(message: String?) {
        JOptionPane.showMessageDialog(
            this,
            message ?: "Unknown error",
            "Error",
            JOptionPane.ERROR_MESSAGE
        )
    }
}
