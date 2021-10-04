package task.a.solve

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

    private fun test(
        inputFile: String,
        expectedFile: String,
    ) {
        val input: String = loadFile(inputFile).first()
        val expected: String = loadFile(expectedFile).first()
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
