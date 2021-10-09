package task.b.solve

import commons.expression.Expression

fun build(
    classicalProof: ClassicalProof,
    hypothesis: Set<Expression>,
): NaturalProof {
    return buildRecur(
        classicalProof,
        hypothesis.toList(),
        0,
    )
}

private fun buildRecur(
    classicalProof: ClassicalProof,
    hypothesis: List<Expression>,
    level: Int,
): NaturalProof {
    return when (classicalProof) {
        is ClassicalProof.HypothesisItem -> {
            NaturalProof.Ax(
                hypothesis,
                classicalProof.expression,
                level,
            )
        }
        is ClassicalProof.AxiomItem -> {
            classicalProof.axiom.naturalProof(
                classicalProof.expression,
                hypothesis,
                level,
            )
        }
        is ClassicalProof.MpItem -> {
            NaturalProof.EraseImp(
                hypothesis,
                classicalProof.expression,
                level,
                buildRecur(classicalProof.imp, hypothesis, level + 1),
                buildRecur(classicalProof.left, hypothesis, level + 1),
            )
        }
    }
}
