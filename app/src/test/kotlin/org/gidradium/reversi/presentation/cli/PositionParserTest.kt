package org.gidradium.reversi.presentation.cli

import org.gidradium.reversi.game.Position
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test

class PositionParserTest {

    @Test
    fun `parses D3`() {
        assertEquals(
            Position(2, 3),
            PositionParser.parse("D3")
        )
    }

    @Test
    fun `parses lowercase position`() {
        assertEquals(
            Position(2, 3),
            PositionParser.parse("d3")
        )
    }

    @Test
    fun `rejects invalid length`() {
        assertThrows<IllegalArgumentException> {
            PositionParser.parse("D33")
        }
    }

    @Test
    fun `rejects invalid row`() {
        assertThrows<IllegalArgumentException> {
            PositionParser.parse("DX")
        }
    }

    @Test
    fun `rejects position outside board`() {
        assertThrows<IllegalArgumentException> {
            PositionParser.parse("I1")
        }
    }
}
