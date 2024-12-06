package fr.amanin.aoc2024.day02

import java.util.function.IntPredicate
import kotlin.math.abs

val TOLERANCE = 3

fun countSafeLevels(input: List<String>, withDampener: Boolean): Int {
    val isSafeOperator = if (withDampener) ::brutForceSafe else { { isSafe(it, remainingRemovals = 0) } }
    return parse(input).asSequence()
        .filter(isSafeOperator)
        .count()
}


fun part1(input: List<String>): Int = countSafeLevels(input, false)
fun part2(input: List<String>): Int = countSafeLevels(input, true)

fun parse(input: List<String>) : List<List<Int>> {
    return input.map {
        it.split(Regex("\\s+")).map { it.toInt() }
    }
}

fun brutForceSafe(levels: List<Int>) : Boolean {
    if (isSafe(levels, remainingRemovals = 0) || isSafe(levels.subList(1, levels.size), remainingRemovals = 0)) return true
    for (i in 1 until levels.size) {
        val dampenedLevels = levels.toMutableList().apply { removeAt(i) }
        if (isSafe(dampenedLevels, remainingRemovals = 0)) return true
    }

    return false
}

fun isSafe(levels: List<Int>, remainingRemovals: Int = 0) : Boolean  {
    require (levels.size >= 2)

    var diff = levels[1] - levels[0]
    val firstSafe = diff != 0 && abs(diff) <= TOLERANCE
    if (levels.size < 3) return firstSafe

    val directionCheck : (Int) -> Boolean = if (diff > 0) { { it > 0 } } else { { it < 0 } }

    var nextDiff = levels[2] - levels[1]
    return if (firstSafe && directionCheck(nextDiff) && abs(nextDiff) <= TOLERANCE) {
        isSafe(levels, 2, directionCheck, remainingRemovals)
    } else if (remainingRemovals < 1) {
        false
    } else {
        isSafe(levels.subList(1, levels.size), remainingRemovals - 1)
                || isSafe(ArrayList(levels).apply { removeAt(1) }, remainingRemovals - 1)
    }
}


private fun isSafe(levels: List<Int>, startIdx: Int, directionCheck: IntPredicate, remainingRemovals: Int = 0) : Boolean  {
    var remainingRemovals = remainingRemovals
    var i = startIdx
    while (++i < levels.size) {
        val diff = levels[i] - levels[i-1]
        if (directionCheck.test(diff) && abs(diff) <= TOLERANCE) continue
        else if (remainingRemovals < 1) return false
        else if (i == levels.lastIndex) return true

        remainingRemovals--

        var diffBis = levels[i + 1] - levels[i - 1]
        if (directionCheck.test(diffBis) && abs(diffBis) <= TOLERANCE) {
            i++ ; continue
        }

        if (i < 2) return false

        diffBis = levels[i] - levels[i-2]
        if (!directionCheck.test(diffBis) || abs(diffBis) > TOLERANCE) return false
    }

    return true
}