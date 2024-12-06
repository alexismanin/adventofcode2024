package fr.amanin.aoc2024.day03

val mulInstruction = Regex("mul\\((?<mulop1>\\d{1,3}),(?<mulop2>\\d{1,3})\\)")

val mulOrCondition = Regex("(?<do>do\\(\\))|(?<dont>don't\\(\\))|${mulInstruction.pattern}")


private fun MatchResult.multiply() = (checkNotNull(groups["mulop1"]) { "First operand not found in mul instruction" }).value.toInt() *
        (checkNotNull(groups["mulop2"]) { "Second operand not found in mul instruction"}).value.toInt()

fun part1(input: List<String>): Long {
    return input.asSequence()
        .flatMap { mulInstruction.findAll(it) }
        .map(MatchResult::multiply)
        .fold(0L, Long::plus)
}

fun part2(input: List<String>) : Long {
    return input.asSequence()
        .flatMap { mulOrCondition.findAll(it) }
        .fold(true to 0L) { (active, total), match ->
            if (match.groups["do"] != null) true to total
            else if (match.groups["dont"] != null) false to total
            else if (!active) false to total
            else true to total + match.multiply()
        }
        .second
}
