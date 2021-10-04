package commons.io

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.Closeable
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter

@Suppress("unused")
class IO(
    inStream: InputStream,
    outStream: OutputStream,
) : Closeable {

    constructor(inFile: String, outFile: String) :
        this(
            FileInputStream(inFile),
            FileOutputStream(outFile),
        )

    constructor(args: Array<String>) :
        this(
            args.getOrNull(0).let { inFile ->
                if (inFile != null)
                    FileInputStream(inFile)
                else
                    System.`in`
            },
            args.getOrNull(1).let { outFile ->
                if (outFile != null)
                    FileOutputStream(outFile)
                else
                    System.out
            },
        )

    private val `in` = BufferedReader(
        InputStreamReader(inStream)
    )
    private val out = BufferedWriter(
        OutputStreamWriter(outStream)
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
