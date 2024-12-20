package fr.amanin.aoc2024

import kotlin.math.max
import kotlin.math.min

data class Vector2D(val row: Int, val col: Int) : Comparable<Vector2D> {
    operator fun plus(other: Vector2D): Vector2D = Vector2D(row + other.row, col + other.col)
    operator fun minus(other: Vector2D) = Vector2D(row - other.row, col - other.col)
    operator fun times(scalar: Int) = Vector2D(row * scalar, col * scalar)
    operator fun div(scalar: Int) = Vector2D(row / scalar, col / scalar)

    override fun compareTo(other: Vector2D) = compareBy(Vector2D::row, Vector2D::col).compare(this, other)
}

interface CharacterGrid {
    val nCols : Int ; val nRows: Int
    operator fun get(row: Int, column: Int): Char
    operator fun get(position: Vector2D): Char
    fun row(index: Int): String
    fun rows(): Sequence<String> = (0..<nRows).asSequence().map { row(it) }
    fun print(): String = rows().joinToString(System.lineSeparator())
}

private class GridView(private val letters: List<String>) : CharacterGrid {
    override val nCols = letters[0].length
    override val nRows = letters.size

    override operator fun get(row: Int, column: Int): Char = letters[row][column]
    override operator fun get(position: Vector2D): Char = letters[position.row][position.col]
    override fun row(index: Int) = letters[index]
}

class MutableGrid(override val nRows: Int, override val nCols: Int, private val buffer : CharArray = CharArray(nRows*nCols)) : CharacterGrid {

    override operator fun get(row: Int, column: Int) = buffer[row * nCols + column]
    override operator fun get(position: Vector2D) = buffer[position.row * nCols + position.col]
    override fun row(index: Int) = String(buffer.copyOfRange(index * nCols, (index + 1) * nCols))

    operator fun set(position: Vector2D, newValue: Char) {
        buffer[position.row * nCols + position.col] = newValue
    }

    fun setRow(row: Int, line: String) {
        require(line.length == nCols)
        line.toCharArray().copyInto(buffer, destinationOffset = row * nCols)
    }
}

fun gridOf(lines: List<String>): CharacterGrid {
    return GridView(lines)
}

fun mutableGridOf(lines: List<String>): MutableGrid {
    val grid = MutableGrid(lines.size, lines[0].length)
    for (i in 0..<lines.size) grid.setRow(i, lines[i])
    return grid
}

fun CharacterGrid.isOnEdge(position: Vector2D): Boolean {
    return position.row == 0 || position.col == 0 || position.row == nRows-1 || position.col == nCols - 1
}

fun CharacterGrid.occurrences(letter: Char) = sequence {
    for (i in 0..<nRows) {
        for (j in 0..<nCols) {
            if (get(i, j) == letter) yield(Vector2D(i, j))
        }
    }
}

fun CharacterGrid.sequence() = sequence {
    for (i in 0..<nRows) {
        for (j in 0..<nCols) {
            yield(Vector2D(i, j) to get(i, j))
        }
    }
}

fun Vector2D.isInside(grid: CharacterGrid) = row in 0..<grid.nRows && col in 0..<grid.nCols
fun Vector2D.isOutside(grid: CharacterGrid) = !isInside(grid)

fun Vector2D.indivisible() = this / pgcd()

fun Vector2D.pgcd() : Int {
    var pgcd = 1
    var minTerm = min(row, col)
    var remainder = max(row, col) % minTerm
    while (remainder > 0) {
        pgcd = remainder
        minTerm = pgcd
        remainder = minTerm % remainder
    }

    return pgcd
}
