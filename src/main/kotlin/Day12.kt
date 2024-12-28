package fr.amanin.aoc2024.day12

import fr.amanin.aoc2024.*
import fr.amanin.aoc2024.day06.GuardDirection

fun part1(lines: List<String>): Long {
    val grid = gridOf(lines)

    return grid.regions()
        .sumOf { it.price }
}

data class RegionValue(val cellValue: Char, val area: Int, val perimeter: Int) {
    val price: Long get() = area.toLong() * perimeter
}

fun CharacterGrid.regions() : Sequence<RegionValue> = sequence {
    val usedMask = MutableGrid(nRows, nCols, CharArray(nRows * nCols) { '0' })
    var nbUsedCells = 0L
    while (nbUsedCells < nCells) {
        val startPoint = usedMask.first { it == '0' } ?: break
        val cellValue = get(startPoint)
        val pointList = expandAdjacent(startPoint, usedMask) { it == cellValue }
            .toList()
        nbUsedCells += pointList.size
        val perimeter = pointList.map {
            pt -> GuardDirection.entries.filter {
                dir ->
                val nextPt = pt + dir.direction
                nextPt.isOutside(this@regions) || get(pt  + dir.direction) != cellValue
            }.size
        }.sum()
        yield(RegionValue(cellValue, pointList.size, perimeter))
    }

    assert(nbUsedCells == nCells)
}

fun CharacterGrid.expandAdjacent(start: Vector2D, mask: MutableGrid, filter: (Char) -> Boolean) = sequence {
    var nextStartPoints = setOf(start)
    mask[start] = '1'
    while (nextStartPoints.isNotEmpty()) {
        yieldAll(nextStartPoints)
        nextStartPoints = nextStartPoints.flatMap {
            pt ->
            GuardDirection.entries
                .mapNotNull {
                    val nextPt = it.direction + pt
                    if (nextPt.isOutside(this@expandAdjacent)) null
                    else {
                        val used = mask[nextPt] != '0'
                        if (!used && filter(get(nextPt))) {
                            mask[nextPt] = '1'
                            nextPt
                        } else null
                    }
                }
        }.toSet()
    }
}