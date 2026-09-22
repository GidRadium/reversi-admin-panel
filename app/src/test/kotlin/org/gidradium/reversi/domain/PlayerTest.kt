package org.gidradium.reversi.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test

class PlayerTest {

    @Test
    fun `creates player with valid name`() {
        val player = Player(1, "Alice")

        assertEquals(1, player.id)
        assertEquals("Alice", player.name)
    }

    @Test
    fun `rejects empty name`() {
        assertThrows<IllegalArgumentException> {
            Player(1, "")
        }
    }

    @Test
    fun `rejects blank name`() {
        assertThrows<IllegalArgumentException> {
            Player(1, "   ")
        }
    }

    @Test
    fun `players with same values are equal`() {
        val first = Player(1, "Alice")
        val second = Player(1, "Alice")

        assertEquals(first, second)
    }
}
