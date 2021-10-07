package task.c.solve

import task.c.dto.*
import task.c.solve.ways.*
import task.c.types.*

fun buildProof(
    statement: Expression,
    proofPrototype: List<Expression>,
): Proof {
    val proved: MutableMap<Expression, Int> = mutableMapOf()
    val rightToImp: MutableMap<Expression, MutableList<Imp>> = mutableMapOf()

    val isMPFun: (Expression) -> CheckResult = { MPChecker.check(proved, rightToImp, it) }
    val isExistsFun: (Expression) -> CheckResult = { RuleChecker.isExistsRule(proved, it) }
    val isForAnyFun: (Expression) -> CheckResult = { RuleChecker.isForAnyRule(proved, it) }

    val finalResult = mutableListOf<ProofItem>()

    for ((index, expression) in proofPrototype.withIndex()) {
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
        val checkerResults = checkers.map { it(expression) }
        val currentResult: ProofItem = checkerResults.firstNotNullOfOrNull { result ->
            if (result is Reason) {
                ProofItem.NormalItem(result, expression)
            } else {
                null
            }
        } ?: run {
            val freeFailedResult = checkerResults.firstNotNullOfOrNull { result ->
                if (result is CheckResult.FreeCheckFailed) {
                    result
                } else {
                    null
                }
            }
            if (freeFailedResult != null) {
                ProofItem.WrongLine("Expression ${index + 1}: ${freeFailedResult.message}.")
            } else {
                ProofItem.WrongLine("Expression ${index + 1} is not proved.")
            }
        }
        finalResult.add(currentResult)
        when (currentResult) {
            is ProofItem.WrongLine -> break
            is ProofItem.NormalItem -> {
                proved.putIfAbsent(expression, index)
                if (expression is Imp) {
                    rightToImp[expression.right]?.add(expression)
                        ?: run { rightToImp[expression.right] = mutableListOf(expression) }
                }
            }
        }
    }

    val last = finalResult.last()
    if (last is ProofItem.NormalItem && last.expression != statement) {
        finalResult.add(ProofItem.WrongLine("The proof proves different expression."))
    }
    return Proof(statement, finalResult)
}
