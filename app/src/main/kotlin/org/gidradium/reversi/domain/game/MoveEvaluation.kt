package org.gidradium.reversi.domain.game

data class MoveEvaluation(
    val isValid: Boolean,
    val flippedCells: List<Position> = emptyList(),
    val reason: String? = null
)
