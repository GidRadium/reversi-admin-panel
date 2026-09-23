package org.gidradium.reversi.presentation.gui

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame

class Gui(
    private val viewModel: GuiViewModel
) {

    private val frame = JFrame("Reversi Admin Panel")

    private val playersPanel = PlayersPanel(
        viewModel = viewModel,
        onChanged = ::refreshAll
    )

    private val gamePanel = GamePanel(
        viewModel = viewModel,
        onChanged = ::refreshAll
    )

    private val gamesPanel = GamesPanel(
        viewModel = viewModel,
        onChanged = ::refreshAll
    )

    fun show() {
        viewModel.refresh()

        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.minimumSize = Dimension(1100, 700)
        frame.setSize(1200, 750)
        frame.setLocationRelativeTo(null)

        frame.layout = BorderLayout()

        frame.add(playersPanel, BorderLayout.WEST)
        frame.add(gamePanel, BorderLayout.CENTER)
        frame.add(gamesPanel, BorderLayout.EAST)

        refreshAll()

        frame.isVisible = true
    }

    private fun refreshAll() {
        playersPanel.refresh()
        gamePanel.refresh()
        gamesPanel.refresh()
    }
}
