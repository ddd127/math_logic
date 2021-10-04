package task.b.solve

import commons.expression.Expression

data class NaturalProof(
    val hypothesis: List<Expression>,
    val result: Expression,
    val rule: Rule,
    val level: Int,
    val precondition: List<NaturalProof>,
) {
    constructor(
        hypothesis: List<Expression>,
        result: Expression,
        rule: Rule,
        level: Int,
        vararg precondition: NaturalProof,
    ) : this(hypothesis, result, rule, level, precondition.toList())

    fun getAllItems(): List<NaturalProof> {
        val result = mutableListOf<NaturalProof>()
        getRecur(result)
        return result
    }

    private fun getRecur(
        result: MutableList<NaturalProof>,
    ) {
        precondition.forEach { it.getRecur(result) }
        result.add(this)
    }

    companion object {

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
                    NaturalProof(
                        hypothesis,
                        classicalProof.expression,
                        Rule.AX,
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
                    NaturalProof(
                        hypothesis,
                        classicalProof.expression,
                        Rule.E_IMP,
                        level,
                        buildRecur(classicalProof.imp, hypothesis, level + 1),
                        buildRecur(classicalProof.left, hypothesis, level + 1),
                    )
                }
            }
        }
    }
}

enum class Rule(
    val sign: String,
) {
    AX("Ax"),
    E_IMP("E->"),
    I_IMP("I->"),
    I_AND("I&"),
    E_L_AND("El&"),
    E_R_AND("Er&"),
    I_L_OR("Il|"),
    I_R_OR("Ir|"),
    E_OR("E|"),
    E_BOTTOM("E_|_"),
}
