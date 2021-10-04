package task.b.solve

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
        val expected: String = loadFile(expectedFile).joinToString("\n")
        val actual: String = ByteArrayInputStream(input.toByteArray(Charsets.UTF_8)).use { inputStream ->
            ByteArrayOutputStream().use { outputStream ->
                IO(inputStream, outputStream).use { io ->
                    Solve(io).solve()
                }
                outputStream.toString(Charsets.UTF_8)
            }
        }
        assertEquals(expected.trimEnd(), actual.trimEnd())
    }

    private fun loadFile(fileName: String): List<String> {
        return javaClass.getResourceAsStream("/$fileName")!!
            .bufferedReader(Charsets.UTF_8).readLines()
    }
}
