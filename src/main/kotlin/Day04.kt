package fr.amanin.aoc2024.day04

import fr.amanin.aoc2024.*

private typealias WordGrid = CharacterGrid

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
    val grid = gridOf(input)
    return grid.occurrences(SEARCHED.first())
        .flatMap { startPosition -> searchWord(grid, startPosition, SEARCHED) }
        .count()
}

fun part2(input: List<String>): Int {
    val grid = gridOf(input)
    return grid.occurrences('A')
        .filter { !grid.isOnEdge(it) }
        .filter { grid.isXMas(it) }
        .count()
}

private fun WordGrid.isXMas(position: Vector2D) : Boolean {
    return checkDiagonal(position, DIAGONAL_1) && checkDiagonal(position, DIAGONAL_2)
}

private fun WordGrid.checkDiagonal(position: Vector2D, diagonal: Vector2D): Boolean {
    val diagonalLetters = "${get(position + diagonal)}${get(position - diagonal)}"
    return diagonalLetters == "MS" || diagonalLetters == "SM"
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

private fun searchWord(grid: WordGrid, start: Vector2D, direction: Vector2D, searched: String) : Boolean {
    for (i in 2..<searched.length) {
        val nextPosition = start + (direction * i)
        if (nextPosition.isOutside(grid) || grid[nextPosition] != searched[i]) return false
    }
    return true
}

private data class Find(val start: Vector2D, val direction: Vector2D)

