package fr.amanin.aoc2024.day01

import java.util.TreeMap
import kotlin.math.abs

val LINE_PATTERN = Regex("\\s*(\\d+)\\s+(\\d+)\\s*")

fun part1(input: List<String>): Any {
   val l1 = ArrayList<Int>(input.size)
   val l2 = ArrayList<Int>(input.size)
   for ((idx, line) in input.withIndex()) {
      val match = LINE_PATTERN.matchEntire(line)
         ?: throw IllegalArgumentException("Malformed input. Line $idx, content: $line")

      l1.add(match.groupValues[1].toInt())
      l2.add(match.groupValues[2].toInt())
   }

   l1.sort()
   l2.sort()

   return l1.indices.asSequence()
      .map { abs(l1[it] - l2[it]) }
      .sum()
}

fun part2(input: List<String>): Any {
   val l1 = TreeMap<Int, Int>()
   val l2 = TreeMap<Int, Int>()
   for ((idx, line) in input.withIndex()) {
      val match = LINE_PATTERN.matchEntire(line)
         ?: throw IllegalArgumentException("Malformed input. Line $idx, content: $line")

      l1.merge(match.groupValues[1].toInt(), 1, Int::plus)
      l2.merge(match.groupValues[2].toInt(), 1, Int::plus)
   }

   var score = 0L
   for ((key, count) in l1) {
      score += count * (key * (l2[key] ?: 0))
   }

   return score
}
