package fr.amanin.aoc2024.day04

private val SEARCHED = "XMAS"
private val DIRECTIONS = arrayOf(
    Vector2D(-1,  0),
    Vector2D(-1, -1),
    Vector2D( 0, -1),
    Vector2D( 1, -1),
    Vector2D( 1,  0),
    Vector2D( 1,  1),
    Vector2D( 0,  1),
    Vector2D(-1,  1),
)

private val DIAGONAL_1 = Vector2D(1, 1)
private val DIAGONAL_2 = Vector2D(1, -1)

fun part1(input: List<String>): Int {
    val grid = WordGrid(input)
    return grid.occurrences(SEARCHED.first())
        .flatMap { startPosition -> searchWord(grid, startPosition, SEARCHED) }
        .count()
}

fun part2(input: List<String>): Int {
    val grid = WordGrid(input)
    return grid.occurrences('A')
        .filter { !grid.isOnEdge(it) }
        .filter { grid.isXMas(it) }
        .count()
}

private fun WordGrid.isOnEdge(position: Vector2D): Boolean {
    return position.row == 0 || position.col == 0 || position.row == nRows-1 || position.col == nCols - 1
}

private fun WordGrid.isXMas(position: Vector2D) : Boolean {
    return checkDiagonal(position, DIAGONAL_1) && checkDiagonal(position, DIAGONAL_2)
}

private fun WordGrid.checkDiagonal(position: Vector2D, diagonal: Vector2D): Boolean {
    val diagonalLetters = "${get(position + diagonal)}${get(position - diagonal)}"
    return diagonalLetters == "MS" || diagonalLetters == "SM"
}

private fun WordGrid.occurrences(letter: Char) = sequence {
    for (i in 0..<nRows) {
        for (j in 0..<nCols) {
            if (get(i, j) == letter) yield(Vector2D(i, j))
        }
    }
}

private fun searchWord(grid: WordGrid, start: Vector2D, searched: String) : Sequence<Find> = sequence {
    assert(grid[start] == searched.first())
    val secondLetter = searched[1]
    for (dir in DIRECTIONS) {
        val nextPosition = start + dir
        if (nextPosition.isOutside(grid)) continue
        if (grid[nextPosition] == secondLetter && searchWord(grid, start, dir, searched)) yield(Find(start, dir))
    }
}

private fun Vector2D.isOutside(grid: WordGrid) = row < 0 || row >= grid.nRows || col < 0 || col >= grid.nCols

private fun searchWord(grid: WordGrid, start: Vector2D, direction: Vector2D, searched: String) : Boolean {
    for (i in 2..<searched.length) {
        val nextPosition = start + (direction * i)
        if (nextPosition.isOutside(grid) || grid[nextPosition] != searched[i]) return false
    }
    return true
}

private data class Find(val start: Vector2D, val direction: Vector2D)

private data class Vector2D(val row: Int, val col: Int) {
    operator fun plus(other: Vector2D): Vector2D = Vector2D(row + other.row, col + other.col)
    operator fun minus(other: Vector2D) = Vector2D(row - other.row, col - other.col)
    operator fun times(scalar: Int) = Vector2D(row * scalar, col * scalar)
}

private class WordGrid(private val letters: List<String>) {
    val nCols = letters[0].length
    val nRows = letters.size

    operator fun get(row: Int, column: Int): Char = letters[row][column]
    operator fun get(position: Vector2D): Char = letters[position.row][position.col]
}