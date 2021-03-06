package task.b.solve

import commons.io.IO
import org.junit.Assert
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object Util {

    fun test(
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
        Assert.assertEquals(expected.trimEnd(), actual.trimEnd())
    }

    private fun loadFile(fileName: String): List<String> {
        return javaClass.getResourceAsStream("/$fileName")!!
            .bufferedReader(Charsets.UTF_8).readLines()
    }
}
