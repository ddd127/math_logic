package task.b.solve

import commons.expression.Expression
import task.b.solve.Axiom.Companion.isAxiom

sealed interface ClassicalProof {

    val expression: Expression

    data class HypothesisItem(
        override val expression: Expression,
    ) : ClassicalProof

    data class AxiomItem(
        override val expression: Expression,
        val axiom: Axiom,
    ) : ClassicalProof

    data class MpItem(
        override val expression: Expression,
        val imp: ClassicalProof,
        val left: ClassicalProof,
    ) : ClassicalProof

    companion object {

        fun buildProof(
            hypothesis: Set<Expression>,
            statement: Expression,
            proofList: List<Expression>,
        ): ClassicalProof {
            val expressionToProof = mutableMapOf<Expression, ClassicalProof>()
            val rightToImp = mutableMapOf<Expression, MutableSet<Expression.Imp>>()
            for ((index, expression) in proofList.withIndex()) {
                if (expressionToProof.containsKey(expression)) {
                    continue
                }
                val axiom = expression.isAxiom()
                expressionToProof[expression] = when {
                    hypothesis.contains(expression) -> {
                        HypothesisItem(expression)
                    }
                    axiom != null -> {
                        AxiomItem(expression, axiom)
                    }
                    else -> {
                        rightToImp[expression]?.firstNotNullOfOrNull { probableParent ->
                            expressionToProof[probableParent.left]?.let { leftProof ->
                                val impProof = expressionToProof.getValue(probableParent)
                                MpItem(expression, impProof, leftProof)
                            }
                        } ?: throw IllegalArgumentException(
                            "Proof is incorrect at line ${index + 2}"
                        )
                    }
                }
                if (expression is Expression.Imp) {
                    rightToImp[expression.right]?.add(expression)
                        ?: run { rightToImp[expression.right] = mutableSetOf(expression) }
                }
            }
            if (proofList.last() != statement) {
                throw IllegalArgumentException(
                    "The proof does not prove the required expression"
                )
            }
            return expressionToProof.getValue(statement)
        }
    }
}
