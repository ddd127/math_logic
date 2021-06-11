package application.io

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

@Suppress("unused")
class IO(
    inFile: String? = null,
    outFile: String? = null
) : Closeable {

    constructor(args: Array<String>) : this(args.getOrNull(0), args.getOrNull(1))

    private val `in` = BufferedReader(
        InputStreamReader(
            if (inFile != null)
                FileInputStream(inFile)
            else
                System.`in`
        )
    )
    private val out = BufferedWriter(
        OutputStreamWriter(
            if (outFile != null)
                FileOutputStream(outFile)
            else
                System.out
        )
    )

    fun readLn(): String = `in`.readLine()
    fun readStrings(): List<String> = readLn().split(" ") // list of strings

    fun readInt() = readLn().toInt() // single int
    fun readLong() = readLn().toLong() // single long
    fun readDouble() = readLn().toDouble() // single double
    fun readInts() = readStrings().map { it.toInt() } // list of ints
    fun readLongs() = readStrings().map { it.toLong() } // list of longs
    fun readDoubles() = readStrings().map { it.toDouble() } // list of doubles
    fun readAllLines() = `in`.readLines()

    fun write(t: Any) = out.write(t.toString())
    fun writeln(t: Any) = out.write("$t\n")
    fun writeln() = out.write("\n")

    override fun close() {
        `in`.close()
        out.close()
    }
}
