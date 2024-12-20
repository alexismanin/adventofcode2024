package fr.amanin.aoc2024.day08

import fr.amanin.aoc2024.*

fun part1(input: List<String>): Long {
    val grid = gridOf(input)
    val antinodes = grid.sequence()
        .filter { (_, letter) -> letter != '.' }
        .groupBy(Pair<Vector2D, Char>::second, Pair<Vector2D, Char>::first)
        .values
        .asSequence()
        .flatMap { it.antinodes() }
        .filter { it.isInside(grid) }
        .toSet()

    val marked = mutableGridOf(input)
    antinodes.forEach { marked[it] = '#' }
    println(marked.print())

    antinodes.sorted().forEach { println(it) }

    return antinodes.count().toLong()
}

fun part2(input: List<String>): Long {
    val grid = gridOf(input)
    val antinodes = grid.sequence()
        .filter { (_, letter) -> letter != '.' }
        .groupBy(Pair<Vector2D, Char>::second, Pair<Vector2D, Char>::first)
        .values
        .asSequence()
        .flatMap { it.harmonicsAntinodes(grid) }
        .toSet()

    val marked = mutableGridOf(input)
    antinodes.forEach { marked[it] = '#' }
    println(marked.print())

    return antinodes.count().toLong()
}

fun List<Vector2D>.pairs() = sequence {
    for (i in indices) {
        val p1 = get(i)
        for (j in i + 1 until size) {
            yield(p1 to get(j))
        }
    }
}

fun List<Vector2D>.antinodes() = pairs()
    .flatMap {
        (p1, p2) ->
        val p1ToP2 = p2 - p1
        listOf(
            p1 - p1ToP2,
            p2 + p1ToP2
        )
    }

fun List<Vector2D>.harmonicsAntinodes(grid: CharacterGrid) = pairs()
    .flatMap {
        (p1, p2) ->
        val p1ToP2 = p2 - p1
        sequence {
            var antinode = p1
            while (antinode.isInside(grid)) {
                yield(antinode)
                antinode -= p1ToP2
            }

            antinode = p2
            while (antinode.isInside(grid)) {
                yield(antinode)
                antinode += p1ToP2
            }
        }
    }
