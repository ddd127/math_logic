package task.c.solve.ways

import task.c.dto.*
import task.c.types.*

object MPChecker {

    private val pairComparator: Comparator<Pair<Int, Int>> =
        compareBy<Pair<Int, Int>> { pair -> pair.first }
            .thenComparing { pair -> pair.second }

    fun check(
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
        return ways.firstOrNull()?.let { (left, imp) ->
            Reason.MP(left + 1, imp + 1)
        } ?: CheckResult.SchemeCheckFailed
    }
}
