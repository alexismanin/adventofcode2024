package fr.amanin.aoc2024

val matchDay = Regex("(?i)(da?y?)?(?<idx>\\d{1,2})")
val matchPart = Regex("(?i)(p(art)?)?(?<idx>\\d{1,2})")
fun main(args: Array<String>) {
    require(args.size == 2) {
        """
            Expect exactly 2 arguments:
              1. The day number (1, 2, ...)
              2. part of the day to execute (1, 2, ...)
        """.trimIndent()
    }
   val dayIdx = matchDay.matchEntire(args[0])
        ?.groups?.get("idx")
        ?.value
        ?.toInt()
        ?: throw IllegalArgumentException("First argument must be the Day to run")

    val partIdx = matchPart.matchEntire(args[1])
    ?.groups?.get("idx")
    ?.value
    ?.toInt()
    ?: throw IllegalArgumentException("Second argument must be the part of the day to run")

    val loader = Thread.currentThread().contextClassLoader
    val result = runDay(dayIdx, partIdx, loader)
    println("Result for day $dayIdx part $partIdx: $result")
}

fun runDay(dayIdx: Int, partIdx: Int, loader: ClassLoader) : Number {
    require(dayIdx in 1..25)
    val dayIdx = if (dayIdx < 10) "0$dayIdx" else "$dayIdx"
    val clazz = try {
        loader.loadClass("fr.amanin.aoc2024.day$dayIdx.Day$dayIdx")
    } catch (e: ClassNotFoundException) {
        loader.loadClass("fr.amanin.aoc2024.day$dayIdx.Day${dayIdx}Kt")
    }
    val target = clazz.getMethod("part$partIdx", List::class.java)

    val inputName = "Day${dayIdx}.txt"
    val inputlines = loader.getResource(inputName)
        ?.readText()?.lines()
        ?.let { if (it.last().isBlank()) it.subList(0, it.lastIndex) else it }
        ?: throw RuntimeException("Input $inputName not found in resources root directory")

    return target.invoke(null, inputlines) as? Number ?: throw RuntimeException("Result is not an integer !")
}
