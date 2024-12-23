package fr.amanin.aoc2024.day10

import fr.amanin.aoc2024.*
import fr.amanin.aoc2024.day06.GuardDirection

fun part1(input: List<String>): Int {
    val grid = gridOf(input)
    println("heads: "+ grid.occurrences('0').count())
    return grid.occurrences('0')
        .map { Trail(it) }
        .map(grid::explore)
        .map(Sequence<Trail>::toList)
        .onEach { grid.print(it) }
        .map { trails -> trails.mapTo(mutableSetOf(), Trail::last) }
        .map { it.size }
        .onEach { println("score: $it") }
        .sum()
}

fun part2(input: List<String>): Int {
    val grid = gridOf(input)
    println("heads: "+ grid.occurrences('0').count())
    return grid.occurrences('0')
        .map { Trail(it) }
        .flatMap(grid::explore)
        .count()
}

data class Trail(val steps: List<Vector2D> = ArrayList(10)) {
    constructor(start: Vector2D) : this(ArrayList<Vector2D>(10).apply { add(start) })
    val last get() = steps.last()

    fun advance(step: Vector2D): Trail = Trail(steps + step)
}

fun CharacterGrid.explore(trail: Trail) : Sequence<Trail> = sequence {
    val value = get(trail.last).digitToInt()
    val nextValue = value + 1
    if (value == 9) {
        yield(trail)
    } else {
        for (direction in GuardDirection.entries) {
            val nextPosition = trail.last + direction.direction
            if (nextPosition.isInside(this@explore) && get(nextPosition).digitToInt() == nextValue) {
                yieldAll(explore(trail.advance(nextPosition)))
            }
        }
    }
}

fun CharacterGrid.print(trails: List<Trail>) {
    val output = MutableGrid(nRows, nCols)
    for (trail in trails) trail.steps.forEach { pt -> output[pt] = get(pt) }
    println(
        """
----
${output.print()}
----
""".trimIndent()
    )
}

fun CharacterGrid.print(trail: Trail) = print(listOf(trail))
