package task.b.solve

import commons.expression.Expression

sealed interface NaturalProof {

    val hypothesis: List<Expression>
    val result: Expression
    val level: Int
    val rule: String

    fun getPreconditions(): List<NaturalProof>

    fun getAllItems(): List<NaturalProof> {
        val result = mutableListOf<NaturalProof>()
        getRecur(result)
        return result
    }

    private fun getRecur(
        result: MutableList<NaturalProof>,
    ) {
        getPreconditions().forEach { it.getRecur(result) }
        result.add(this)
    }

    data class Ax(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = emptyList()

        override val rule: String = "Ax"
    }

    data class EraseImp(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val imp: NaturalProof,
        val left: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(imp, left)

        override val rule: String = "E->"
    }

    data class InsertImp(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val precondition: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(precondition)

        override val rule: String = "I->"
    }

    data class InsertAnd(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val phi: NaturalProof,
        val psi: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(phi, psi)

        override val rule: String = "I&"
    }

    data class EraseLeftAnd(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val precondition: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(precondition)

        override val rule: String = "El&"
    }

    data class EraseRightAnd(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val precondition: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(precondition)

        override val rule: String = "Er&"
    }

    data class InsertLeftOr(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val precondition: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(precondition)

        override val rule: String = "Il|"
    }

    data class InsertRightOr(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val precondition: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(precondition)

        override val rule: String = "Ir|"
    }

    data class EraseOr(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val phi: NaturalProof,
        val psi: NaturalProof,
        val or: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(phi, psi, or)

        override val rule: String = "E|"
    }

    data class EraseBottom(
        override val hypothesis: List<Expression>,
        override val result: Expression,
        override val level: Int,
        val precondition: NaturalProof,
    ) : NaturalProof {

        override fun getPreconditions(): List<NaturalProof> = listOf(precondition)

        override val rule: String = "E_|_"
    }
}
