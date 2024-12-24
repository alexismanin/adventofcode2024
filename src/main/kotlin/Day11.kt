package fr.amanin.aoc2024.day11

import java.util.TreeMap

fun part1(input: List<String>) = blink(input, 25)

// TODO: use dynamic programming to cache tree parts.
fun part2(input: List<String>) = blinkParallel(input, 75)

fun blinkParallel(input: List<String>, times: Int) : Long {
    assert(input.size == 1)

    val queue = ArrayDeque<Long>()

    val initialState = parse(input.first()).toList()

    val threads = ArrayList<Thread>(initialState.size)
    var numThread = 1
    for (stone in initialState) {
        val threadId = numThread++
        val t = Thread {
            val count = blink(listOf(stone.text), times, "T$threadId")
            queue.add(count)
        }
        threads.add(t)
        t.start()
    }

    for (thread in threads) {
        thread.join()
    }

    return queue.sum()
}

fun blink(input: List<String>, times: Int, label : String = ""): Long {
    assert(input.size == 1)
    var state = parse(input.first()).toList()
    val cache = TreeMap(mutableMapOf(0L to CacheMatch(listOf(Stone(1L)))))
    println("INITIAL SEQUENCE:\n\t${state.joinToString(" ") { stone -> stone.value.toString() }}")
    for (i in 1..times) {
        state = state.flatMap { it.shift(cache) }
        if (i == 38) {
            println(cache.entries
                         .sortedByDescending { it.value.hits }
                         .subList(0, 20)
                         .joinToString("\n\t", "[$label] CACHE:\n\t") {
                             (key, match) -> "KEY: $key, MATCHED: ${match.hits} times, VALUES: ${match.values}"
                         })
        }

        println("[$label] Blinked $i times")
    }

    return state.count().toLong()
}

fun parse(line: String) : Sequence<Stone> {
    return Regex("\\s+")
        .splitToSequence(line)
        .filter { it.isNotBlank() }
        .map { Stone(it.toLong()) }
}

@JvmInline
value class Stone(val value: Long) {
    val text: String get() = "$value"

    override fun toString() = text
}

data class CacheMatch(val values: List<Stone>, var hits: Long = 0) {
    fun hit() { hits += 1L }
}
fun Stone.shift(cache: MutableMap<Long, CacheMatch>) : Sequence<Stone> = sequence {
    // HARD-CODED cases have been added after analysis of top cache hits, in order.
    when (value) {
        4L -> yield(Stone(8096L))
        8L -> yield(Stone(16192L))
        2L -> yield(Stone(4048L))
        0L -> yield(Stone(1L))
        6L -> yield(Stone(12144L))
        8096L -> {
            yield(Stone(80L))
            yield(Stone(96L))
        }
        1L -> yield(Stone(2024L))
        16192L -> yield(Stone(32772608L))
        4048L -> {
            yield(Stone(40L))
            yield(Stone(48L))
        }
        12144L -> yield(Stone(24579456L))
        80L -> {
            yield(Stone(8L))
            yield(Stone(0L))
        }
        7L -> yield(Stone(14168L))
        48L -> {
            yield(Stone(4L))
            yield(Stone(8L))
        }
        9L -> yield(Stone(18216))
        24L -> {
            yield(Stone(2L))
            yield(Stone(4L))
        }
        96L -> {
            yield(Stone(9L))
            yield(Stone(6L))
        }
        2024L -> {
            yield(Stone(20L))
            yield(Stone(24L))
        }
        32772608L -> {
            yield(Stone(3277L))
            yield(Stone(2608L))
        }
        40L -> {
            yield(Stone(4L))
            yield(Stone(0L))
        }
        24579456L -> {
            yield(Stone(2457L))
            yield(Stone(9456L))
        }
        20L -> {
            yield(Stone(2L))
            yield(Stone(0L))
        }
        else -> {
            var next = cache[value]
            if (next == null) {
                val text = text
                if (text.length % 2 != 0) next = CacheMatch(listOf(Stone(value * 2024L)))
                else {
                    val middle = text.length/2
                    next = CacheMatch(listOf(Stone(text.substring(0, middle).toLong()),
                        Stone(text.substring(middle).toLong())))
                }
                cache[value] = next
            }

            next.hit()
            yieldAll(next.values)
        }
    }
}
