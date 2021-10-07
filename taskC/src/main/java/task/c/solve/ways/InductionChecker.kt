package task.c.solve.ways

import task.c.*
import task.c.types.*

object InductionChecker {

    fun check(expression: Expression): CheckResult {
        if (expression !is Imp ||
            expression.left !is And ||
            expression.left.right !is ForAny ||
            expression.left.right.expression !is Imp
        ) {
            return CheckResult.SchemeCheckFailed
        }
        val variable = expression.left.right.variable
        val phi = expression.right
        if (!phi.isFree(variable)) {
            return CheckResult.SchemeCheckFailed
        }
        val phiZeroReplaced = phi.replaceFree(variable, Zero)
        val phiIncReplaced = phi.replaceFree(variable, Inc(variable))
        return if (expression.left.left == phiZeroReplaced &&
            expression.left.right.expression.left == phi &&
            expression.left.right.expression.right == phiIncReplaced
        ) {
            Reason.Induction
        } else {
            CheckResult.SchemeCheckFailed
        }
    }
}
