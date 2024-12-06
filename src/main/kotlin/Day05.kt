package fr.amanin.aoc2024.day05

fun part1(input: List<String>): Int {
    val input = parse(input)
    return input.pageUpdates.asSequence()
        .filter { update ->
            for ((key, values) in input.pageOrderingRules) {
                val keyFirstOccurrence = update.indexOf(key)
                if (keyFirstOccurrence == -1) continue
                for (value in values) {
                    val valueFirstOccurrence = update.indexOf(value)
                    if (valueFirstOccurrence in 0..<keyFirstOccurrence) return@filter false
                }
            }
            return@filter true
        }

        .map { update -> update[update.size / 2]}
        .sum()
}

fun part2(input: List<String>): Int {
    val input = parse(input)
    TODO()
}

fun parse(input: List<String>): PuzzleInput {
    val rules = mutableMapOf<Int, MutableSet<Int>>()
    var i = 0
    while (i < input.size) {
        val line = input[i++]
        if (line.isBlank()) break
        val pair = line.split('|')
        check(pair.size == 2)
        val key = pair[0].toInt()
        val value = pair[1].toInt()
        rules.computeIfAbsent(key) { mutableSetOf() }.add(value)
    }

    val updates = mutableListOf<List<Int>>()
    while (i < input.size) {
        val line = input[i++]
        val update = line.split(',').map(String::toInt)
        updates.add(update)
    }

    return PuzzleInput(rules, updates)
}

data class PuzzleInput(val pageOrderingRules: Map<Int, Set<Int>>, val pageUpdates: List<List<Int>>)
