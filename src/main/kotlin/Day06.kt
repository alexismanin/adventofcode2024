package fr.amanin.aoc2024.day06

import fr.amanin.aoc2024.Vector2D
import fr.amanin.aoc2024.mutableGridOf

enum class GuardDirection(val symbol: Char) {
    left('<'), up('^'), right('>'), down('v')
}

fun part1(lines: List<String>): Int {
    val grid = mutableGridOf(lines)
    print(grid.print())
    TODO()
}
