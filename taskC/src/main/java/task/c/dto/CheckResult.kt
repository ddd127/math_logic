package task.c.dto

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
