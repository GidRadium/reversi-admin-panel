package org.gidradium.reversi.game

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test

class PositionTest {

    @Test
    fun `accepts top left position`() {
        val position = Position(0, 0)

        assertEquals(0, position.row)
        assertEquals(0, position.column)
    }

    @Test
    fun `accepts bottom right position`() {
        val position = Position(7, 7)

        assertEquals(7, position.row)
        assertEquals(7, position.column)
    }

    @Test
    fun `rejects negative row`() {
        assertThrows<IllegalArgumentException> {
            Position(-1, 0)
        }
    }

    @Test
    fun `rejects row outside board`() {
        assertThrows<IllegalArgumentException> {
            Position(8, 0)
        }
    }

    @Test
    fun `rejects negative column`() {
        assertThrows<IllegalArgumentException> {
            Position(0, -1)
        }
    }

    @Test
    fun `rejects column outside board`() {
        assertThrows<IllegalArgumentException> {
            Position(0, 8)
        }
    }

    @Test
    fun `positions with same values are equal`() {
        val first = Position(2, 3)
        val second = Position(2, 3)

        assertEquals(first, second)
    }
}
