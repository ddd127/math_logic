package task.a.solve

import commons.expression.Expression
import commons.io.IO

class Solve(
    private val io: IO,
) {
    fun solve() {
        Expression.parseClassical(io.readLn())
            .run {
                io.writeln(this.mathString)
            }
    }
}
