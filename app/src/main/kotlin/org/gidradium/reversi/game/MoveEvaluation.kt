package org.gidradium.reversi.game

data class MoveEvaluation(
    val isValid: Boolean,
    val flippedCells: List<Position> = emptyList(),
    val reason: String? = null
)
