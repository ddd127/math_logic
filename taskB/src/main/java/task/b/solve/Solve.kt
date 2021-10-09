package task.b.solve

import commons.expression.Expression
import commons.io.IO

class Solve(
    private val io: IO,
) {
    fun solve() {
        try {
            val allLines = io.readAllLines().filter { it.isNotEmpty() && it.isNotBlank() }
            val firstLine = allLines[0]
            val (hyposString, statementString) = firstLine.split("|-")
            val hypothesisSet = hyposString.split(",")
                .filter { it.isNotEmpty() && it.isNotBlank() }
                .map(Expression.Companion::parseClassical)
                .toSet()
            val statement = Expression.Companion.parseClassical(statementString)
            val proofList = allLines.drop(1).map { line ->
                Expression.Companion.parseClassical(line)
            }
            val classicalProof = ClassicalProof.buildProof(
                hypothesisSet,
                statement,
                proofList,
            )
            val naturalProof = build(classicalProof, hypothesisSet)
            val items = naturalProof.getAllItems()
            items.forEach { item ->
                item.run {
                    val hypoString = hypothesis.joinToString(",") { it.naturalString() }
                    io.writeln(
                        "[$level] $hypoString|-${result.naturalString()} [${item.rule}]"
                    )
                }
            }
        } catch (e: Exception) {
            e.message?.let(io::writeln)
                ?: io.write("Unexpected exception found: $e")
        }
    }
}
