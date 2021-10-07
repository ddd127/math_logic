package task.c.solve

import commons.io.IO
import task.c.types.*

import org.junit.Test

import org.junit.Assert.*
import task.c.Solve
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class SolveTest {

    @Test
    fun test1() {

    }

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
