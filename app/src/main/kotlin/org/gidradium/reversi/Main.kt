package org.gidradium.reversi

import org.gidradium.reversi.application.AdminService
import org.gidradium.reversi.application.database.DatabaseFactory
import org.gidradium.reversi.application.repository.ExposedGameRepository
import org.gidradium.reversi.application.repository.ExposedPlayerRepository
import org.gidradium.reversi.presentation.gui.Gui
import org.gidradium.reversi.presentation.gui.GuiViewModel
import javax.swing.SwingUtilities
import javax.swing.UIManager

fun main() {
    setLookAndFeel()

    val database = DatabaseFactory.connect()
    DatabaseFactory.initialize(database)

    val adminService = AdminService(
        playerRepository = ExposedPlayerRepository(database),
        gameRepository = ExposedGameRepository(database)
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
