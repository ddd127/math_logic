package task.c

import commons.io.IO
import task.c.dto.*
import task.c.types.*

class Solve(
    private val io: IO
) {

    fun solve() {
        val allLines = io.readAllLines().map { string ->
            string.filterNot(Char::isWhitespace)
        }
        if (allLines.size < 2) {
            io.write("Input size less than 2 lines")
            return
        }
        val firstLine = allLines.first()
        val statement = firstLine.drop(2).let(Expression::parseExpression)
        val proofProto = allLines.drop(1).map(Expression::parseExpression)

        val proof = buildProof(statement, proofProto)

        io.writeln("|-${proof.statement.infixString}")
        proof.proof.forEachIndexed { indexProto, item ->
            val index = indexProto + 1
            when (item) {
                is ProofItem.WrongLine -> io.write(item.message)
                is ProofItem.NormalItem -> {
                    val (reason, expression) = item
                    val annotation = when (reason) {
                        is Reason.LogicAxiom -> "$index. Ax. sch. ${reason.schemeNumber}"
                        is Reason.Induction -> "$index. Ax. sch. A9"
                        is Reason.ArithmeticAxiom -> "$index. Ax. A${reason.schemeNumber}"
                        is Reason.MP -> "$index. M.P. ${reason.left}, ${reason.imp}"
                        is Reason.ForAnyRule -> "$index. @-intro ${reason.number}"
                        is Reason.ExistsRule -> "$index. ?-intro ${reason.number}"
                    }
                    io.writeln("[$annotation] ${expression.infixString}")
                }
            }
        }
    }
}
