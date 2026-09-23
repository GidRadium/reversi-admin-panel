package org.gidradium.reversi.presentation.gui

import org.gidradium.reversi.application.GameId
import org.gidradium.reversi.application.GameRecord
import org.gidradium.reversi.application.PlayerId
import org.gidradium.reversi.application.PlayerStatistics
import org.gidradium.reversi.game.MoveEvaluation
import org.gidradium.reversi.game.Position

data class GuiState(
    val players: Map<PlayerId, String> = emptyMap(),
    val games: List<GameRecord> = emptyList(),
    val selectedPlayerId: PlayerId? = null,
    val selectedGameId: GameId? = null,
    val selectedPlayerStatistics: PlayerStatistics? = null,
    val availableMoves: Set<Position> = emptySet(),
    val lastMoveEvaluation: MoveEvaluation? = null,
    val message: String = ""
)
