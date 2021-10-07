package task.c.solve

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import org.junit.Test

import commons.io.IO

import org.junit.Assert.*

class SolveTest {

    @Test
    fun test1() = test(
        "test1_in.txt",
        "test1_out.txt",
    )

    @Test
    fun test2() = test(
        "test2_in.txt",
        "test2_out.txt",
    )

    @Test
    fun test3() = test(
        "test3_in.txt",
        "test3_out.txt",
    )

    @Test
    fun test4() = test(
        "test4_in.txt",
        "test4_out.txt",
    )

    @Test
    fun test5() = test(
        "test5_in.txt",
        "test5_out.txt",
    )

    @Test
    fun test6() = test(
        "test6_in.txt",
        "test6_out.txt",
    )

    @Test
    fun test7() = test(
        "test7_in.txt",
        "test7_out.txt",
    )

    @Test
    fun test8() = test(
        "test8_in.txt",
        "test8_out.txt",
    )

    private fun test(
        inputFile: String,
        expectedFile: String,
    ) {
        val input: String = loadFile(inputFile).joinToString("\n")
        val expectedLines: List<String> = loadFile(expectedFile).joinToString("\n")
            .trimEnd().split("\n")
        val actualLines: List<String> = ByteArrayInputStream(input.toByteArray(Charsets.UTF_8)).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                IO(inputStream, outputStream).use { io ->
                    Solve(io).solve()
                }
                outputStream.toString(Charsets.UTF_8)
            }
        }.trimEnd().split("\n")


        if (expectedLines.size != actualLines.size) {
            assertEquals(
                expectedLines.joinToString("\n"),
                actualLines.joinToString("\n"),
            )
        }

        for (index in expectedLines.indices) {
            val expected = expectedLines[index]
            val actual = actualLines[index]
            if (expected == actual) {
                assertEquals(expected, actual)
                continue
            }
            try {
                val (expectedAnnotation, expectedExpression) = split(expected)
                val (actualAnnotation, actualExpression) = split(actual)

                assertEquals(expectedExpression, actualExpression)
                checkAnnotation(expectedAnnotation, actualAnnotation, expectedLines, actualLines)
            } catch (e: Exception) {
                throw Exception("Exception at index = $index", e)
            }
        }
    }

    private fun split(string: String): Pair<String, String> {
        val endOfAnnotation = string.indexOfFirst { it == ']' }
        return string.substring(0, endOfAnnotation + 1) to
            string.substring(endOfAnnotation + 1)
    }

    private fun checkAnnotation(
        expected: String,
        actual: String,
        expectedLines: List<String>,
        actualLines: List<String>,
    ) {
        val (expectedWithoutIndex, expectedIndex) = extractFirstIndex(expected)
        val (actualWithoutIndex, actualIndex) = extractFirstIndex(actual)

        assertTrue(actualIndex <= expectedIndex)
        assertEquals(expectedWithoutIndex, actualWithoutIndex)
        assertEquals(
            split(expectedLines[expectedIndex]).second,
            split(actualLines[actualIndex]).second,
        )
    }

    private fun extractFirstIndex(annotation: String): Pair<String, Int> {
        var count = 0
        val indexBegin = annotation.indexOfFirst { char ->
            if (char == ' ') {
                ++count
                count == 2
            } else {
                false
            }
        } + 1
        val indexEnd = annotation.indexOfFirst { char ->
            when (char) {
                ']' -> true
                ',' -> true
                else -> false
            }
        }
        val index = annotation.substring(indexBegin, indexEnd).toInt()
        return annotation.substring(0, indexBegin) + annotation.substring(indexEnd) to index
    }

    private fun loadFile(fileName: String): List<String> {
        return javaClass.getResourceAsStream("/$fileName")!!
            .bufferedReader(Charsets.UTF_8).readLines()
    }
}
