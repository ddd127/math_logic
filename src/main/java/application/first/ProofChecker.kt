package application.first

import application.expression.grammar
import application.io.IO
import com.github.h0tk3y.betterParse.grammar.parseToEnd

private lateinit var io: IO

fun main(args: Array<String>) {
    io = IO(args)
    solve()
    io.close()
}

fun solve() {
    val expr = grammar.parseToEnd(io.readLn().filter { !it.isWhitespace() })
    io.writeln(expr.toMathString())
}
