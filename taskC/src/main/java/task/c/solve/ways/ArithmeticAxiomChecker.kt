package task.c.solve.ways

import task.c.*
import task.c.types.Expression

object ArithmeticAxiomChecker {

    private val schemes: List<Expression> = listOf(
        "a = b -> a' = b'",
        "a = b -> a = c -> b = c",
        "a' = b' -> a = b",
        "! (a' = 0)",
        "a + b' = (a + b)'",
        "a + 0 = a",
        "a * 0 = 0",
        "a * b' = a * b + a",
    ).map { scheme ->
        scheme.let(Expression::parseExpression)
    }

    fun check(expression: Expression): CheckResult {
        return schemes.withIndex().firstOrNull { scheme ->
            scheme == expression
        }?.index?.let { Reason.ArithmeticAxiom(it + 1) } ?: CheckResult.SchemeCheckFailed
    }
}
