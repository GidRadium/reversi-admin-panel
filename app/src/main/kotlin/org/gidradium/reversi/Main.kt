package org.gidradium.reversi

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.repository.InMemoryGameRepository
import org.gidradium.reversi.application.repository.InMemoryPlayerRepository
import org.gidradium.reversi.presentation.gui.Gui
import org.gidradium.reversi.presentation.gui.GuiViewModel
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    setLookAndFeel()

    val adminService = AdminService(
        playerRepository = InMemoryPlayerRepository(),
        gameRepository = InMemoryGameRepository()
    )

    val viewModel = GuiViewModel(adminService)

    SwingUtilities.invokeLater {
        Gui(viewModel).show()
    }
}

private fun setLookAndFeel() {
    val nimbus = UIManager
        .getInstalledLookAndFeels()
        .firstOrNull { it.name == "Nimbus" }

    if (nimbus != null) {
        UIManager.setLookAndFeel(nimbus.className)
    }
}
