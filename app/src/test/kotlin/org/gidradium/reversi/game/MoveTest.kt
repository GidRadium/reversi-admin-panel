package org.gidradium.reversi.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MoveTest {

    @Test
    fun `move stores position and player`() {
        val position = Position(2, 3)
        val move = Move(position, PlayerColor.BLACK)

        assertEquals(position, move.position)
        assertEquals(PlayerColor.BLACK, move.player)
    }
}
