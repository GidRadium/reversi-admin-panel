package org.gidradium.reversi.presentation.gui

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JFrame

private const val MIN_WINDOW_WIDTH = 1100
private const val MIN_WINDOW_HEIGHT = 700

private const val WINDOW_WIDTH = 1200
private const val WINDOW_HEIGHT = 750

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

        configureFrame()
        addPanels()
        refreshAll()

        frame.isVisible = true
    }

    private fun configureFrame() {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        frame.minimumSize = Dimension(
            MIN_WINDOW_WIDTH,
            MIN_WINDOW_HEIGHT
        )

        frame.setSize(
            WINDOW_WIDTH,
            WINDOW_HEIGHT
        )

        frame.setLocationRelativeTo(null)
    }

    private fun addPanels() {
        frame.layout = BorderLayout()

        frame.add(playersPanel, BorderLayout.WEST)
        frame.add(gamePanel, BorderLayout.CENTER)
        frame.add(gamesPanel, BorderLayout.EAST)
    }

    private fun refreshAll() {
        playersPanel.refresh()
        gamePanel.refresh()
        gamesPanel.refresh()
    }
}
