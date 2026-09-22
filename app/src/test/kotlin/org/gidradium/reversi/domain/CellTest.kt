package org.gidradium.reversi.domain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CellTest {

    @Test
    fun `cell has three possible states`() {
        assertEquals(3, Cell.entries.size)
    }
}
