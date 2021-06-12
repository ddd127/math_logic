package application.second

import application.second.expression.grammar
import application.io.IO
import application.second.expression.Expression
import com.github.h0tk3y.betterParse.grammar.parseToEnd

private lateinit var io: IO

fun main(args: Array<String>) {
    io = IO(args)
    solve()
    io.close()
}

fun solve() {
    val allLines = io.readAllLines().map { str -> str.filter { !it.isWhitespace() } }
    val firstLine = allLines.first()
    val expressionToProof = grammar.parseToEnd(firstLine.split("|-")[1])
    val hypotheses = firstLine.split("|-")
        .first().split(",")
        .map { str -> grammar.parseToEnd(str) }
        .toSet()
    val proof = allLines.drop(1)
        .map { str -> grammar.parseToEnd(str) }

    if (!checkProof(expressionToProof, hypotheses, proof)) {
        return
    }
}

private fun checkProof(
    expressionToProof: Expression,
    hypothesis: Set<Expression>,
    proof: List<Expression>,
): Boolean {
    val implRightToLeft = mutableMapOf<Expression, MutableSet<Expression>>()
    val previous = mutableSetOf<Expression>()
    return proof.withIndex().all { (index, expression) ->
        val result = expression.isAxiom() ||
            hypothesis.contains(expression) ||
            (implRightToLeft[expression]?.any { left ->
                previous.contains(left)
            } == true)
        previous.add(expression)
        if (expression is Expression.Imp) {
            implRightToLeft.getOrPut(expression.right) { mutableSetOf() }
                .add(expression.left)
        }
        if (!result) {
            io.writeln("Proof is incorrect at line ${index + 2}")
        }
        result
    } && (proof.last() == expressionToProof)
}
