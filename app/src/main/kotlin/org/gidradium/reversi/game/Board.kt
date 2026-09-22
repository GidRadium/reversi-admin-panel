package org.gidradium.reversi.game

class Board {

    private val cells = Array(SIZE) {
        Array(SIZE) { Cell.EMPTY }
    }

    init {
        cells[3][3] = Cell.WHITE
        cells[3][4] = Cell.BLACK
        cells[4][3] = Cell.BLACK
        cells[4][4] = Cell.WHITE
    }

    operator fun get(position: Position): Cell =
        cells[position.row][position.column]

    internal operator fun set(position: Position, cell: Cell) {
        cells[position.row][position.column] = cell
    }

    fun count(cell: Cell): Int =
        cells.sumOf { row ->
            row.count { it == cell }
        }

    fun snapshot(): List<List<Cell>> =
        cells.map { it.toList() }

    companion object {
        const val SIZE = 8
    }
}
