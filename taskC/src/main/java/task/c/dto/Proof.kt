package task.c.dto

import task.c.types.Expression

sealed interface ProofItem {

    data class NormalItem(
        val reason: Reason,
        val expression: Expression,
    ) : ProofItem

    data class WrongLine(
        val message: String,
    ) : ProofItem
}

data class Proof(
    val statement: Expression,
    val proof: List<ProofItem>,
)
