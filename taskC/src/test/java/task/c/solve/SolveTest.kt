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

    @Test
    fun test9() = test(
        "test9_in.txt",
        "test9_out.txt",
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

        // assertEquals(
        //     expectedLines.joinToString("\n"),
        //     actualLines.joinToString("\n"),
        // )

        assertEquals(expectedLines.size, actualLines.size)
        for (index in expectedLines.indices) {
            val expected = expectedLines[index]
            val actual = actualLines[index]
            if (expected == actual) {
                assertEquals(expected, actual)
                continue
            }
            val (expectedAnnotation, expectedExpression) = split(expected)
            val (actualAnnotation, actualExpression) = split(actual)
            assertEquals(expectedExpression, actualExpression)
            val (expectedNoIndex, expectedIndex) = removeFirstMPIndex(expectedAnnotation)
            val (actualNoIndex, actualIndex) = removeFirstMPIndex(actualAnnotation)
            assertEquals(expectedNoIndex, actualNoIndex)
            assertTrue(actualIndex <= expectedIndex)
            assertEquals(
                split(expectedLines[expectedIndex]).second,
                split(actualLines[actualIndex]).second,
            )
        }
    }

    private fun split(string: String): Pair<String, String> {
        val endOfAnnotation = string.indexOfFirst { it == ']' }
        return string.substring(0, endOfAnnotation + 1) to
            string.substring(endOfAnnotation + 1)
    }

    private fun removeFirstMPIndex(annotation: String): Pair<String, Int> {
        assertTrue(annotation.matches("\\[\\d*\\]. ".toRegex()))
        val mpIndex = annotation.indexOf('M')
        val prefix = annotation.substring(0, mpIndex + "M.P. ".length)
        val postfix = annotation.substring(mpIndex + "M.P. ".length)
        val commaIndex = postfix.indexOf(',')
        val index = postfix.substring(0, commaIndex).toInt()
        return prefix + postfix.substring(commaIndex) to index
    }

    private fun loadFile(fileName: String): List<String> {
        return javaClass.getResourceAsStream("/$fileName")!!
            .bufferedReader(Charsets.UTF_8).readLines()
    }
}
