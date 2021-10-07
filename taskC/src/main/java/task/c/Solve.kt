package task.c

import commons.io.IO
import task.c.types.Expression
import task.c.Reason.*

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

        val proof = try {
            Proof.buildProof(statement, proofProto)
        } catch (e: IllegalArgumentException) {
            e.message?.let(io::write)
            return
        }

        io.writeln("|-${proof.statement.infixString}")
        proof.proof.forEachIndexed { index, pair ->
            val (expression, reason) = pair
            val annotation = when (reason) {
                is LogicAxiom -> "$index. Ax. sch. ${reason.schemeNumber}"
                is Induction -> "$index. Ax. sch. A9"
                is ArithmeticAxiom -> "$index. Ax. A${reason.schemeNumber}"
                is MP -> "$index. M.P. ${reason.left}, ${reason.imp}"
                is ForAnyRule -> "$index. @-intro ${reason.number}"
                is ExistsRule -> "$index. ?-intro ${reason.number}"
            }
            io.writeln("[$annotation] ${expression.infixString}")
        }
    }
}
