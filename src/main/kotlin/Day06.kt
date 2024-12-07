package fr.amanin.aoc2024.day06

import fr.amanin.aoc2024.*

val PATROLLED = 'X'
val OBSTACLE = '#'
enum class GuardDirection(val symbol: Char, val direction: Vector2D) {
    left('<', Vector2D(0, -1)),
    up('^', Vector2D(-1, 0)),
    right('>', Vector2D(0, 1)),
    down('v', Vector2D(1, 0))
}

data class GuardStatus(val position: Vector2D, val direction: Vector2D)

fun MutableGrid.patrol() {
    var current = findGuardPosition()

    while (!current.position.isOutside(this)) {
        set(current.position, PATROLLED)
        val next = current.position + current.direction
        if (next.isOutside(this)) break
        val nextStatus = get(next)
        current = if (nextStatus == OBSTACLE) GuardStatus(current.position, current.direction.turnRight())
                  else GuardStatus(current.position + current.direction, current.direction)
    }
}

/**
 * Apply a clocwise 90° rotation on a 2D vector
 */
fun Vector2D.turnRight() = Vector2D(col, row * -1)

fun CharacterGrid.findGuardPosition() : GuardStatus {
    val symbols = GuardDirection.entries.associate { it.symbol to it }
    for (i in 0..<nRows) {
        for (j in 0..<nCols) {
            val symbol = symbols[get(i, j)]
            if (symbol != null) return GuardStatus(Vector2D(i, j), symbol.direction)
        }
    }

    throw IllegalStateException("No guard found on grid !")
}

fun part1(lines: List<String>): Int {
    val grid = mutableGridOf(lines)
    println("PATROL START")
    println(grid.print())
    grid.patrol()
    println("PATROL END")
    println(grid.print())
    return grid.occurrences(PATROLLED).count()
}
