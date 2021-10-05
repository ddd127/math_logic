package task.c.solve

// import java.util.Locale
//
// import commons.expression.Expression
//
// enum class Axiom(
//     val scheme: Expression
// ) {
//
//     Axiom1(
//         Expression.parseClassical(
//             "A -> B -> A"
//         ).replaceVars(),
//     ),
//
//     Axiom2(
//         Expression.parseClassical(
//             "(A -> B) -> (A -> B -> C) -> (A -> C)"
//         ).replaceVars(),
//     ),
//
//     Axiom3(
//         Expression.parseClassical(
//             "A -> B -> A & B"
//         ).replaceVars(),
//     ),
//
//     Axiom4(
//         Expression.parseClassical(
//             "A & B -> A"
//         ).replaceVars(),
//     ),
//
//     Axiom5(
//         Expression.parseClassical(
//             "A & B -> B"
//         ).replaceVars(),
//     ),
//
//     Axiom6(
//         Expression.parseClassical(
//             "A -> A | B"
//         ).replaceVars(),
//     ),
//
//     Axiom7(
//         Expression.parseClassical(
//             "B -> A | B"
//         ).replaceVars(),
//     ),
//
//     Axiom8(
//         Expression.parseClassical(
//             "(A -> C) -> (B -> C) -> (A | B -> C)"
//         ).replaceVars(),
//     ),
//
//     Axiom9(
//         Expression.parseClassical(
//             "(A -> B) -> (A -> !B) -> !A"
//         ).replaceVars(),
//     ),
//
//     Axiom10(
//         Expression.parseClassical(
//             "A -> !A -> B"
//         ).replaceVars(),
//     ),
//     ;
//
//     companion object {
//
//         fun Expression.isAxiom(): Axiom? {
//             for (value in values()) {
//                 if (matchesScheme(this, value.scheme) != null) {
//                     return value
//                 }
//             }
//             return null
//         }
//
//         private fun matchesScheme(
//             expression: Expression,
//             axiomScheme: Expression,
//         ): Map<String, Expression>? {
//             if (axiomScheme is Expression.Var) {
//                 return mapOf(axiomScheme.name to expression)
//             }
//             if (expression is Expression.Not && axiomScheme is Expression.Not) {
//                 return matchesScheme(expression.arg, axiomScheme.arg)
//             } else if (expression is Expression.And && axiomScheme is Expression.And) {
//                 return processMaps(
//                     matchesScheme(expression.left, axiomScheme.left),
//                     matchesScheme(expression.right, axiomScheme.right),
//                 )
//             } else if (expression is Expression.Or && axiomScheme is Expression.Or) {
//                 return processMaps(
//                     matchesScheme(expression.left, axiomScheme.left),
//                     matchesScheme(expression.right, axiomScheme.right),
//                 )
//             } else if (expression is Expression.Imp && axiomScheme is Expression.Imp) {
//                 return processMaps(
//                     matchesScheme(expression.left, axiomScheme.left),
//                     matchesScheme(expression.right, axiomScheme.right),
//                 )
//             } else {
//                 return null
//             }
//         }
//
//         private fun processMaps(
//             leftMap: Map<String, Expression>?,
//             rightMap: Map<String, Expression>?,
//         ): Map<String, Expression>? {
//             if (leftMap == null || rightMap == null) {
//                 return null
//             }
//             val resultMap = mutableMapOf<String, Expression>()
//             for (key in (leftMap.keys + rightMap.keys)) {
//                 val leftValue = leftMap[key]
//                 val rightValue = rightMap[key]
//                 if (leftValue != null &&
//                     rightValue != null &&
//                     leftValue != rightValue
//                 ) {
//                     return null
//                 }
//                 if (leftValue != null) {
//                     resultMap[key] = leftValue
//                 } else if (rightValue != null) {
//                     resultMap[key] = rightValue
//                 }
//             }
//             return resultMap
//         }
//     }
// }
//
// private fun Expression.replaceVars(): Expression {
//
//     return when (this) {
//         is Expression.Var -> this.copy(name = "Ax_var_${this.name}")
//         is Expression.Not -> this.copy(arg = this.arg.replaceVars())
//         is Expression.And -> this.copy(
//             left = this.left.replaceVars(),
//             right = this.right.replaceVars(),
//         )
//         is Expression.Or -> this.copy(
//             left = this.left.replaceVars(),
//             right = this.right.replaceVars(),
//         )
//         is Expression.Imp -> this.copy(
//             left = this.left.replaceVars(),
//             right = this.right.replaceVars(),
//         )
//         is Expression.Bottom -> this
//     }
// }
