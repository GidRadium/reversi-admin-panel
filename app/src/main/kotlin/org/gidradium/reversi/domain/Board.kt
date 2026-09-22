package org.gidradium.reversi.domain

class Board {

    private val cells: Array<Array<Cell>> =
        Array(SIZE) { Array(SIZE) { Cell.EMPTY } }

    init {
        cells[3][3] = Cell.WHITE
        cells[3][4] = Cell.BLACK
        cells[4][3] = Cell.BLACK
        cells[4][4] = Cell.WHITE
    }

    operator fun get(position: Position): Cell {
        return cells[position.row][position.column]
    }

    internal operator fun set(position: Position, cell: Cell) {
        cells[position.row][position.column] = cell
    }

    companion object {
        const val SIZE = 8
    }
}
