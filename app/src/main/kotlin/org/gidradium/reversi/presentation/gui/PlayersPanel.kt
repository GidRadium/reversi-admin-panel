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

private const val PANEL_GAP = 8
private const val PANEL_PADDING = 10
private const val PANEL_WIDTH = 260

class PlayersPanel(
    private val viewModel: GuiViewModel,
    private val onChanged: () -> Unit
) : JPanel(BorderLayout(PANEL_GAP, PANEL_GAP)) {

    private val playerListModel = DefaultListModel<String>()
    private val playerList = JList(playerListModel)

    private val nameField = JTextField()
    private val statisticsLabel = JLabel("No player selected")

    private var refreshing = false

    init {
        border = BorderFactory.createEmptyBorder(
            PANEL_PADDING,
            PANEL_PADDING,
            PANEL_PADDING,
            PANEL_PADDING
        )
        preferredSize = Dimension(PANEL_WIDTH, 0)

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
                val selectedIndex = viewModel.state.players.keys
                    .indexOf(selectedPlayerId)

                if (selectedIndex >= 0) {
                    playerList.selectedIndex = selectedIndex
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

            val selectedIndex = playerList.selectedIndex

            if (selectedIndex >= 0) {
                viewModel.selectPlayer(playerIdAt(selectedIndex))
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

        val buttonsPanel = JPanel(
            GridLayout(1, 2, PANEL_GAP, PANEL_GAP)
        )

        buttonsPanel.add(createButton)
        buttonsPanel.add(deleteButton)

        val bottomPanel = JPanel(
            BorderLayout(PANEL_GAP, PANEL_GAP)
        )

        bottomPanel.add(
            statisticsLabel,
            BorderLayout.NORTH
        )

        bottomPanel.add(
            nameField,
            BorderLayout.CENTER
        )

        bottomPanel.add(
            buttonsPanel,
            BorderLayout.SOUTH
        )

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
