package fr.amanin.aoc2024.day09

import kotlin.math.min

val DEBUG = true

fun part1(input: List<String>): Long = compute(input, forbidFragmentation = false)
fun part2(input: List<String>): Long = compute(input, forbidFragmentation = true)

fun compute(input: List<String>, forbidFragmentation : Boolean): Long {
    require(input.size == 1) {
        "This puzzle requires a single line of input"
    }

    val initialState = parse(input.first()).toList()

    val compacted = if (DEBUG) {
        initialState.compact(forbidFragmentation)
            .toList()
            .apply {
                if (!forbidFragmentation) checkNoRemainingFreeSpace()
                println(joinToString("", "", "") { it.print() })
            }
            .asSequence()
    } else initialState.compact(forbidFragmentation)

    return compacted.checksum()
}


fun List<MutableBlock>.compact(forbidFragmentation: Boolean) : Sequence<MutableBlock> {
    val compacted = if (forbidFragmentation) ArrayList(this).apply {compactWithoutFragmentation() }.asSequence()
                    else compactWithFragmentation()
    return compacted.filter { block -> block.size > 0 }
}

fun List<MutableBlock>.compactWithFragmentation() : Sequence<MutableBlock>  = sequence {
    var (lastIndex, endSpace) = if (get(lastIndex) is FreeSpace) lastIndex - 1 to get(lastIndex)
                                else lastIndex to FreeSpace(0)
    for (i in 0..lastIndex) {
        val block = get(i)
        if (block.size < 1) continue
        when (block) {
            is FileBlock -> yield(block)
            is FreeSpace -> {
                var j = lastIndex
                while (block.size > 0 && j > i) {
                    val lastBlock = get(j)
                    if (lastBlock.size < 1 || lastBlock !is FileBlock) {
                        j--
                        continue
                    }


                    val moveSize = min(block.size, lastBlock.size)
                    assert(moveSize > 0)

                    block.size -= moveSize
                    lastBlock.size -= moveSize
                    endSpace.size += moveSize

                    yield(FileBlock(lastBlock.id, moveSize))

                    if (lastBlock.size < 1) j--
                }
                lastIndex = j
                if (block.size > 0) yield(block)
            }
        }
    }
    yield(endSpace)
}.filter { it.size > 0 }


fun MutableList<MutableBlock>.compactWithoutFragmentation() {
    var i = 0
    do {
        val block = get(i)
        if (block.size < 1 || block is FileBlock) continue
        assert(block is FreeSpace)
        var j = lastIndex
        while (block.size > 0 && j > i) {
            val lastBlock = get(j)
            if (lastBlock.size !in 1..block.size || lastBlock !is FileBlock) {
                j--
                continue
            }

            block.size -= lastBlock.size
            set(j, FreeSpace(lastBlock.size))
            add(i++, lastBlock)
        }
    } while (++i < size)
}

fun List<MutableBlock>.checkNoRemainingFreeSpace() {
    var freeIdx = size
    for (i in 0..lastIndex) {
        val block = get(i)
        if (block.size < 1) continue
        when (get(i)) {
            is FreeSpace -> freeIdx = min(freeIdx, i)
            is FileBlock -> check(i < freeIdx) {
                "Some free space block remains on block $freeIdx"
            }
        }
    }
}

fun Sequence<MutableBlock>.checksum() : Long {
    val result = fold(0L to 0L) {
            (i, checksum), block ->
        var nextChecksum = checksum
        if (block is FileBlock) {
            for (j in 0..<block.size) {
                nextChecksum += (i + j) * block.id
            }
        }

        i + block.size to nextChecksum
    }
    return result.second
}

fun parse(input: String) = sequence<MutableBlock> {
    var i = 0
    do {
        yield(FileBlock(i/2, input[i++].digitToInt()))
        yield(FreeSpace(input[i++].digitToInt()))
    } while (i < input.lastIndex)

    if (i < input.length) yield(FileBlock(i/2, input.last().digitToInt()))
}

sealed interface MutableBlock { var size: Int }
data class FreeSpace(override var size: Int) : MutableBlock
data class FileBlock(val id: Int, override var size: Int) : MutableBlock

fun MutableBlock.print() = when (this) {
    is FileBlock -> "$id".repeat(size)
    is FreeSpace -> ".".repeat(size)
}
