package task.c.solve.ways

import task.c.dto.*
import task.c.types.*

object QuantifierAxiomChecker {

    fun checkForAny(expression: Expression): CheckResult {
        if (expression !is Imp || expression.left !is ForAny) {
            return CheckResult.SchemeCheckFailed
        }
        val variable = expression.left.variable
        val phi = expression.left.expression

        return checkThetaReplacement(variable, phi, expression.right, 11, '@')
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
        val replacements = phi.extractFreeReplacements(replacedPhi, variable)
            ?: return CheckResult.SchemeCheckFailed
        val theta = when {
            replacements.isEmpty() -> return Reason.LogicAxiom(axNumber)
            replacements.size == 1 -> replacements.first()
            else -> return CheckResult.SchemeCheckFailed
        }
        val quantifiers = phi.extractFreeQuantifiers(variable)
        return if (theta.extractVariables().any { it in quantifiers }) {
            CheckResult.FreeCheckFailed(
                "variable ${variable.name} is not free for term ${theta.infixString} in $axSymbol-axiom"
            )
        } else {
            Reason.LogicAxiom(axNumber)
        }
    }
}
