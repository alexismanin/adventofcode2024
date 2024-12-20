package fr.amanin.aoc2024.day07

fun part1(input: List<String>) = solve(input, listOf(Operator.plus, Operator.times))
fun part2(input: List<String>) = solve(input, Operator.entries)

fun solve(input: List<String>, allowedOps : List<Operator>): Long {
    return input.asSequence()
        .map { parse(it) }
        .mapNotNull { eq -> eq.solutions(true, allowedOps).firstOrNull() }
        .sumOf { it.result }
}

fun combinations(length: Int, ops: List<Operator>) = sequence {
    val chain = IntArray(length)
    do {
        yield(chain.map(ops::get))
        if (chain[0] < ops.lastIndex) chain[0] += 1
        else {
            chain[0] = 0
            var j = 1
            while (j < chain.size && chain[j] >= ops.lastIndex) {
                chain[j++] = 0
            }
            if (j > chain.lastIndex) break
            chain[j] += 1
        }
    } while (true)
}

fun CalibrationEquation.solutions(validOnly: Boolean, ops: List<Operator>) : Sequence<Solution> {
    val combinations = combinations(terms.size - 1, ops).map {
        ops ->
        Solution(ops, ops.withIndex().fold(terms[0]) { left, (idx, op) -> op.functor(left, terms[idx+1])})
    }
    return if (validOnly) combinations.filter { it.result == result } else combinations
}


fun parse(line: String): CalibrationEquation {
    val (result, terms) = line.split(Regex(":\\s+"), limit = 2)
    return CalibrationEquation(result.toLong(), terms.split(Regex("\\s+")).map { it.toLong() })
}

enum class Operator(val symbol: Char, val functor: (Long, Long) -> Long) {
    plus('+', Long::plus),
    times('*', Long::times),
    concat('|', { v1, v2 -> "$v1$v2".toLong() });
}

data class Solution(val operators: List<Operator>, val result: Long)
data class CalibrationEquation(val result: Long, val terms: List<Long>)
