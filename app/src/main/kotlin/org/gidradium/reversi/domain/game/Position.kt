package org.gidradium.reversi.domain.game

data class Position(
    val row: Int,
    val column: Int
) {
    init {
        require(row in 0..7) {
            "Row must be between 0 and 7"
        }

        require(column in 0..7) {
            "Column must be between 0 and 7"
        }
    }
}
