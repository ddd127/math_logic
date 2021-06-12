package application.second

import application.second.expression.grammar
import application.io.IO
import com.github.h0tk3y.betterParse.grammar.parseToEnd

private lateinit var io: IO

fun main(args: Array<String>) {
    io = IO(args)
    try {
        solve()
    } catch (e: Exception) {
    }
    io.close()
}

fun solve() {
    val allLines = io.readAllLines().map { str -> str.filter { !it.isWhitespace() } }
    val firstLine = allLines.first()
    val expressionToProof = grammar.parseToEnd(firstLine.split("|-")[1])
    val hypotheses = firstLine.split("|-")
        .first().split(",")
        .map { str -> grammar.parseToEnd(str) }
    val proof = allLines.drop(1)
        .map { str -> grammar.parseToEnd(str) }

    // io.writeln(hypotheses.joinToString("\n") { expression -> expression.toMathString() })
    // io.writeln()
    // io.writeln(expressionToProof.toMathString())
    // io.writeln()
    // io.writeln(proof.joinToString("\n") { expression -> expression.toMathString() })


}
