package task.c.solve.ways

import task.c.dto.*
import task.c.types.*

object RuleChecker {

    fun isForAnyRule(
        proved: Map<Expression, Int>,
        expression: Expression,
    ): CheckResult {
        if (expression !is Imp || expression.right !is ForAny) {
            return CheckResult.SchemeCheckFailed
        }
        val phi = expression.left
        val psi = expression.right.expression
        val variable = expression.right.variable
        return if (phi.entersFree(variable)) {
            CheckResult.FreeCheckFailed("variable ${variable.name} occurs free in @-rule")
        } else {
            proved[Imp(phi, psi)]?.let { Reason.ForAnyRule(it + 1) }
                ?: CheckResult.SchemeCheckFailed
        }
    }

    fun isExistsRule(
        proved: Map<Expression, Int>,
        expression: Expression,
    ): CheckResult {
        if (expression !is Imp || expression.left !is Exists) {
            return CheckResult.SchemeCheckFailed
        }
        val phi = expression.right
        val psi = expression.left.expression
        val variable = expression.left.variable
        return if (phi.entersFree(variable)) {
            CheckResult.FreeCheckFailed("variable ${variable.name} occurs free in ?-rule")
        } else {
            proved[Imp(psi, phi)]?.let { Reason.ExistsRule(it + 1) }
                ?: CheckResult.SchemeCheckFailed
        }
    }
}
