package org.gidradium.reversi.application

import org.gidradium.reversi.infrastructure.repository.InMemoryPlayerRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class PlayerServiceTest {

    private val repository = InMemoryPlayerRepository()
    private val service = PlayerService(repository)

    @Test
    fun `creates player`() {
        val player = service.createPlayer("Alice")

        assertEquals(1, player.id)
        assertEquals("Alice", player.name)
    }

    @Test
    fun `finds player`() {
        val created = service.createPlayer("Alice")

        val found = service.getPlayer(created.id)

        assertNotNull(found)
        assertEquals(created, found)
    }

    @Test
    fun `returns all players`() {
        service.createPlayer("Alice")
        service.createPlayer("Bob")

        val players = service.getAllPlayers()

        assertEquals(2, players.size)
    }
}
