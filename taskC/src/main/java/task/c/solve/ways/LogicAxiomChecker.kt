package task.c.solve.ways

import task.c.dto.*
import task.c.types.*

object LogicAxiomChecker {

    private val schemes: List<Expression> = listOf(
        "A -> B -> A",
        "(A -> B) -> (A -> B -> C) -> (A -> C)",
        "A -> B -> A & B",
        "A & B -> A",
        "A & B -> B",
        "A -> A | B",
        "B -> A | B",
        "(A -> C) -> (B -> C) -> (A | B -> C)",
        "(A -> B) -> (A -> !B) -> !A",
        "!!A -> A",
    ).map { scheme ->
        scheme.let(Expression::parseExpression)
            .let(this::replaceVars)
    }

    fun check(expression: Expression): CheckResult {
        return schemes.withIndex().firstOrNull { (_, scheme) ->
            matchesScheme(expression, scheme) != null
        }?.index?.let { Reason.LogicAxiom(it + 1) } ?: CheckResult.SchemeCheckFailed
    }

    private fun matchesScheme(
        expression: Expression,
        axiomScheme: Expression,
    ): Map<String, Expression>? {
        if (axiomScheme is Predicate) {
            return mapOf(axiomScheme.name to expression)
        }
        if (expression is Not && axiomScheme is Not) {
            return matchesScheme(expression.arg, axiomScheme.arg)
        } else if (expression is And && axiomScheme is And) {
            return processMaps(
                matchesScheme(expression.left, axiomScheme.left),
                matchesScheme(expression.right, axiomScheme.right),
            )
        } else if (expression is Or && axiomScheme is Or) {
            return processMaps(
                matchesScheme(expression.left, axiomScheme.left),
                matchesScheme(expression.right, axiomScheme.right),
            )
        } else if (expression is Imp && axiomScheme is Imp) {
            return processMaps(
                matchesScheme(expression.left, axiomScheme.left),
                matchesScheme(expression.right, axiomScheme.right),
            )
        } else {
            return null
        }
    }

    private fun processMaps(
        leftMap: Map<String, Expression>?,
        rightMap: Map<String, Expression>?,
    ): Map<String, Expression>? {
        if (leftMap == null || rightMap == null) {
            return null
        }
        val resultMap = mutableMapOf<String, Expression>()
        for (key in (leftMap.keys + rightMap.keys)) {
            val leftValue = leftMap[key]
            val rightValue = rightMap[key]
            if (leftValue != null &&
                rightValue != null &&
                leftValue != rightValue
            ) {
                return null
            }
            if (leftValue != null) {
                resultMap[key] = leftValue
            } else if (rightValue != null) {
                resultMap[key] = rightValue
            }
        }
        return resultMap
    }

    private fun replaceVars(expression: Expression): Expression {
        return when (expression) {
            is Predicate -> expression.copy(name = "Ax_var_${expression.name}")
            is Not -> expression.copy(
                arg = replaceVars(expression.arg),
            )
            is And -> expression.copy(
                left = replaceVars(expression.left),
                right = replaceVars(expression.right),
            )
            is Or -> expression.copy(
                left = replaceVars(expression.left),
                right = replaceVars(expression.right),
            )
            is Imp -> expression.copy(
                left = replaceVars(expression.left),
                right = replaceVars(expression.right),
            )
            else -> throw IllegalStateException("Impossible")
        }
    }
}
