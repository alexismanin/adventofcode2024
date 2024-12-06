package fr.amanin.aoc2024

import fr.amanin.aoc2024.day02.brutForceSafe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class TestDays {


    @ParameterizedTest(name="Day {0} part {1} should return {2}")
    @CsvSource(
        "1, 1, 11",
        "1, 2, 31",
        "2, 1, 2",
        "2, 2, 4",
        "3, 1, 161",
        "3, 2, 48",
    )
    fun testDay(dayIdx: Int, partIdx: Int, expectedOutput: Long) {
        val result = runDay(dayIdx, partIdx, Thread.currentThread().contextClassLoader)
        assertEquals(expectedOutput, result.toLong())
    }

    @Test
    fun debugDay2MissingAbsoluteValue() {
        assertFalse { brutForceSafe(listOf(9, 7, 6, 2, 1)) }
    }
}