package task.c.solve.ways

import task.c.CheckResult
import task.c.Reason
import task.c.types.*

object QuantifierAxiomChecker {

    fun checkForAny(expression: Expression): CheckResult {
        if (expression !is Imp || expression.left !is ForAny) {
            return CheckResult.SchemeCheckFailed
        }
        val variable = expression.left.variable
        val phi = expression.left.expression

        return checkThetaReplacement(variable, phi, expression.right, 11, '!')
    }

    fun checkExists(expression: Expression): CheckResult {
        if (expression !is Imp || expression.right !is Exists) {
            return CheckResult.SchemeCheckFailed
        }
        val variable = expression.right.variable
        val phi = expression.right.expression

        return checkThetaReplacement(variable, phi, expression.left, 12, '?')
    }

    private fun checkThetaReplacement(
        variable: Var,
        phi: Expression,
        replacedPhi: Expression,
        axNumber: Int,
        axSymbol: Char,
    ): CheckResult {
        val extractedTerms = phi.extractTerms(replacedPhi, variable) ?: return CheckResult.SchemeCheckFailed
        val theta = extractedTerms.let {
            if (it.size != 1) {
                return CheckResult.SchemeCheckFailed
            } else {
                it.first()
            }
        }
        return if (phi.isFreeForReplace(variable, theta)) {
            Reason.LogicAxiom(axNumber)
        } else {
            CheckResult.FreeCheckFailed(
                "variable $variable is not free for term $theta in $axSymbol-axiom"
            )
        }
    }
}
