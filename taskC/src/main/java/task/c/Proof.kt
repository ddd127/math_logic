package task.c

import task.c.solve.ways.*
import task.c.types.*

sealed interface CheckResult {

    object SchemeCheckFailed : CheckResult

    data class FreeCheckFailed(
        val message: String,
    ) : CheckResult
}

sealed interface Reason : CheckResult {

    data class LogicAxiom(
        val schemeNumber: Int,
    ) : Reason

    object Induction : Reason

    data class ArithmeticAxiom(
        val schemeNumber: Int,
    ) : Reason

    data class MP(
        val left: Int,
        val imp: Int,
    ) : Reason

    data class ForAnyRule(
        val number: Int,
    ) : Reason

    data class ExistsRule(
        val number: Int,
    ) : Reason
}

data class Proof(
    val statement: Expression,
    val proof: List<Pair<Expression, Reason>>,
) {
    companion object {

        private val pairComparator: Comparator<Pair<Int, Int>> =
            compareBy<Pair<Int, Int>> { pair -> pair.first }
                .thenComparing { pair -> pair.second }

        fun buildProof(
            statement: Expression,
            proofPrototype: List<Expression>,
        ): Proof {
            val proved: Map<Expression, Int> = mutableMapOf()
            val rightToImp: Map<Expression, List<Imp>> = mutableMapOf()

            val isMPFun: (Expression) -> CheckResult = { isMP(proved, rightToImp, it) }
            val isExistsFun: (Expression) -> CheckResult = { isExistsRule(proved, it) }
            val isForAnyFun: (Expression) -> CheckResult = { isForAnyRule(proved, it) }
            val proof = proofPrototype.mapIndexed { index, expression ->
                val checkers = listOf(
                    LogicAxiomChecker::check,
                    QuantifierAxiomChecker::checkForAny,
                    QuantifierAxiomChecker::checkExists,
                    InductionChecker::check,
                    ArithmeticAxiomChecker::check,
                    isMPFun,
                    isExistsFun,
                    isForAnyFun,
                ).asSequence()
                val results = checkers.map { it(expression) }
                return@mapIndexed results.firstNotNullOfOrNull { result ->
                    if (result is Reason) {
                        expression to result
                    } else {
                        null
                    }
                } ?: run {
                    val freeFailedResult = results.firstNotNullOfOrNull { result ->
                        if (result is CheckResult.FreeCheckFailed) {
                            result
                        } else {
                            null
                        }
                    }
                    throw if (freeFailedResult != null) {
                        IllegalArgumentException("Expression $index: ${freeFailedResult.message}.")
                    } else {
                        IllegalArgumentException("Expression $index is not proved.")
                    }
                }
            }
            if (proof.last().first != statement) {
                throw IllegalArgumentException("The proof proves different expression.")
            }
            return Proof(statement, proof)
        }

        private fun isForAnyRule(
            proved: Map<Expression, Int>,
            expression: Expression,
        ): CheckResult {
            if (expression !is Imp || expression.right !is ForAny) {
                return CheckResult.SchemeCheckFailed
            }
            val phi = expression.left
            val psi = expression.right.expression
            val variable = expression.right.variable
            return if (containsFree(phi, variable)) {
                CheckResult.FreeCheckFailed("variable ${variable.name} occurs free in @-rule")
            } else {
                proved[Imp(phi, psi)]?.let { Reason.ForAnyRule(it) } ?: CheckResult.SchemeCheckFailed
            }
        }

        private fun isExistsRule(
            proved: Map<Expression, Int>,
            expression: Expression,
        ): CheckResult {
            if (expression !is Imp || expression.left !is Exists) {
                return CheckResult.SchemeCheckFailed
            }
            val phi = expression.right
            val psi = expression.left.expression
            val variable = expression.left.variable
            return if (containsFree(phi, variable)) {
                CheckResult.FreeCheckFailed("variable ${variable.name} occurs free in ?-rule")
            } else {
                proved[Imp(psi, phi)]?.let { Reason.ExistsRule(it) } ?: CheckResult.SchemeCheckFailed
            }
        }

        private fun containsFree(
            expression: Expression,
            variable: Var,
        ): Boolean = expression.extractVariables().contains(variable) &&
            expression.isFree(variable)

        private fun isMP(
            proved: Map<Expression, Int>,
            rightToImp: Map<Expression, List<Imp>>,
            expression: Expression,
        ): CheckResult {
            val imps = rightToImp[expression] ?: return CheckResult.SchemeCheckFailed
            val ways = imps.mapNotNull { imp ->
                val leftIndex = proved[imp.left] ?: return@mapNotNull null
                val impIndex = proved.getValue(imp)
                leftIndex to impIndex
            }.sortedWith(pairComparator)
            return ways.firstOrNull()?.let { Reason.MP(it.first, it.second) } ?: CheckResult.SchemeCheckFailed
        }
    }
}
