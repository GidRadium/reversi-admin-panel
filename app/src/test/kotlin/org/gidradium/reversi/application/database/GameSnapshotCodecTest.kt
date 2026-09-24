package org.gidradium.reversi.application.database

import org.gidradium.reversi.game.Board
import org.gidradium.reversi.game.Cell
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GameSnapshotCodecTest {

    @Test
    fun `encode and decode preserve board`() {
        val board = Board().snapshot()

        val encoded = GameSnapshotCodec.encodeBoard(board)
        val decoded = GameSnapshotCodec.decodeBoard(encoded)

        assertEquals(board, decoded)
    }

    @Test
    fun `encoded initial board contains 64 cells`() {
        val board = Board().snapshot()

        val encoded = GameSnapshotCodec.encodeBoard(board)

        assertEquals(64, encoded.length)
    }

    @Test
    fun `empty board is encoded correctly`() {
        val board = List(8) {
            List(8) {
                Cell.EMPTY
            }
        }

        val encoded = GameSnapshotCodec.encodeBoard(board)

        assertEquals(
            "E".repeat(64),
            encoded
        )
    }

    @Test
    fun `decode rejects board with invalid length`() {
        assertThrows<IllegalArgumentException> {
            GameSnapshotCodec.decodeBoard("E")
        }
    }

    @Test
    fun `decode rejects unknown cell value`() {
        val invalidBoard = "E".repeat(63) + "X"

        assertThrows<IllegalStateException> {
            GameSnapshotCodec.decodeBoard(invalidBoard)
        }
    }

    @Test
    fun `encode rejects board with invalid number of rows`() {
        val invalidBoard = List(7) {
            List(8) {
                Cell.EMPTY
            }
        }

        assertThrows<IllegalArgumentException> {
            GameSnapshotCodec.encodeBoard(invalidBoard)
        }
    }

    @Test
    fun `encode rejects board with invalid row size`() {
        val invalidBoard = List(8) { row ->
            if (row == 0) {
                List(7) {
                    Cell.EMPTY
                }
            } else {
                List(8) {
                    Cell.EMPTY
                }
            }
        }

        assertThrows<IllegalArgumentException> {
            GameSnapshotCodec.encodeBoard(invalidBoard)
        }
    }
}
