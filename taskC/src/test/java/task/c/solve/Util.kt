package task.c.solve

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

import commons.io.IO
import org.junit.Assert

object Util {

    fun test(
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

        Assert.assertEquals(
            expectedLines.joinToString("\n"),
            actualLines.joinToString("\n"),
        )
    }

    private fun loadFile(fileName: String): List<String> {
        return javaClass.getResourceAsStream("/$fileName")!!
            .bufferedReader(Charsets.UTF_8).readLines()
    }
}
