package task.b.solve

import java.util.Locale

import commons.expression.Expression

enum class Axiom(
    val scheme: Expression
) {

    Axiom1(
        Expression.parseClassical(
            "A -> B -> A"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            return NaturalProof.InsertImp(
                hypothesis,
                expression,
                level,
                NaturalProof.InsertImp(
                    hypothesis + a,
                    Expression.Imp(b, a),
                    level + 1,
                    NaturalProof.Ax(
                        hypothesis + a + b,
                        a,
                        level + 2,
                    ),
                ),
            )
        }
    },

    Axiom2(
        Expression.parseClassical(
            "(A -> B) -> (A -> B -> C) -> (A -> C)"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val c = map.getValue("c")
            val h = hypothesis +
                Expression.Imp(a, b) +
                Expression.Imp(a, Expression.Imp(b, c)) +
                a
            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    Expression.Imp(a, b),
                    Expression.Imp(
                        Expression.Imp(
                            a,
                            Expression.Imp(b, c),
                        ),
                        Expression.Imp(a, c),
                    ),
                ),
                level,
                NaturalProof.InsertImp(
                    h.dropLast(2),
                    Expression.Imp(
                        Expression.Imp(
                            a,
                            Expression.Imp(b, c),
                        ),
                        Expression.Imp(a, c),
                    ),
                    level + 1,
                    NaturalProof.InsertImp(
                        h.dropLast(1),
                        Expression.Imp(a, c),
                        level + 2,
                        NaturalProof.EraseImp(
                            h,
                            c,
                            level + 3,
                            NaturalProof.EraseImp(
                                h,
                                Expression.Imp(b, c),
                                level + 4,
                                NaturalProof.Ax(
                                    h,
                                    Expression.Imp(a, Expression.Imp(b, c)),
                                    level + 5,
                                ),
                                NaturalProof.Ax(
                                    h,
                                    a,
                                    level + 5,
                                ),
                            ),
                            NaturalProof.EraseImp(
                                h,
                                b,
                                level + 4,
                                NaturalProof.Ax(
                                    h,
                                    Expression.Imp(a, b),
                                    level + 5,
                                ),
                                NaturalProof.Ax(
                                    h,
                                    a,
                                    level + 5,
                                ),
                            ),
                        ),
                    ),
                ),
            )
        }
    },

    Axiom3(
        Expression.parseClassical(
            "A -> B -> A & B"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val h = hypothesis + a + b
            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    a,
                    Expression.Imp(
                        b,
                        Expression.And(a, b),
                    )
                ),
                level,
                NaturalProof.InsertImp(
                    hypothesis + a,
                    Expression.Imp(
                        b,
                        Expression.And(a, b),
                    ),
                    level + 1,
                    NaturalProof.InsertAnd(
                        h,
                        Expression.And(a, b),
                        level + 2,
                        NaturalProof.Ax(
                            h,
                            a,
                            level + 3,
                        ),
                        NaturalProof.Ax(
                            h,
                            b,
                            level + 3,
                        ),
                    )
                ),
            )
        }
    },

    Axiom4(
        Expression.parseClassical(
            "A & B -> A"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val h = hypothesis + Expression.And(a, b)
            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    Expression.And(a, b),
                    a,
                ),
                level,
                NaturalProof.EraseLeftAnd(
                    h,
                    a,
                    level + 1,
                    NaturalProof.Ax(
                        h,
                        Expression.And(a, b),
                        level + 2,
                    )
                )
            )
        }
    },

    Axiom5(
        Expression.parseClassical(
            "A & B -> B"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val h = hypothesis + Expression.And(a, b)
            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    Expression.And(a, b),
                    b,
                ),
                level,
                NaturalProof.EraseRightAnd(
                    h,
                    b,
                    level + 1,
                    NaturalProof.Ax(
                        h,
                        Expression.And(a, b),
                        level + 2,
                    )
                )
            )
        }
    },

    Axiom6(
        Expression.parseClassical(
            "A -> A | B"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val h = hypothesis + a
            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    a,
                    Expression.Or(a, b),
                ),
                level,
                NaturalProof.InsertLeftOr(
                    h,
                    Expression.Or(a, b),
                    level + 1,
                    NaturalProof.Ax(
                        h,
                        a,
                        level + 2,
                    )
                )
            )
        }
    },

    Axiom7(
        Expression.parseClassical(
            "B -> A | B"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val h = hypothesis + b
            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    b,
                    Expression.Or(a, b),
                ),
                level,
                NaturalProof.InsertRightOr(
                    h,
                    Expression.Or(a, b),
                    level + 1,
                    NaturalProof.Ax(
                        h,
                        b,
                        level + 2,
                    )
                )
            )
        }
    },

    Axiom8(
        Expression.parseClassical(
            "(A -> C) -> (B -> C) -> (A | B -> C)"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val c = map.getValue("c")
            val h = hypothesis +
                Expression.Imp(a, c) +
                Expression.Imp(b, c) +
                Expression.Or(a, b)

            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    Expression.Imp(a, c),
                    Expression.Imp(
                        Expression.Imp(b, c),
                        Expression.Imp(
                            Expression.Or(a, b),
                            c,
                        ),
                    ),
                ),
                level,
                NaturalProof.InsertImp(
                    h.dropLast(2),
                    Expression.Imp(
                        Expression.Imp(b, c),
                        Expression.Imp(
                            Expression.Or(a, b),
                            c,
                        ),
                    ),
                    level + 1,
                    NaturalProof.InsertImp(
                        h.dropLast(1),
                        Expression.Imp(
                            Expression.Or(a, b),
                            c,
                        ),
                        level + 2,
                        NaturalProof.EraseOr(
                            h,
                            c,
                            level + 3,
                            NaturalProof.EraseImp(
                                h + a,
                                c,
                                level + 4,
                                NaturalProof.Ax(
                                    h + a,
                                    Expression.Imp(a, c),
                                    level + 5,
                                ),
                                NaturalProof.Ax(
                                    h + a,
                                    a,
                                    level + 5,
                                ),
                            ),
                            NaturalProof.EraseImp(
                                h + b,
                                c,
                                level + 4,
                                NaturalProof.Ax(
                                    h + b,
                                    Expression.Imp(b, c),
                                    level + 5,
                                ),
                                NaturalProof.Ax(
                                    h + b,
                                    b,
                                    level + 5,
                                ),
                            ),
                            NaturalProof.Ax(
                                h,
                                Expression.Or(a, b),
                                level + 4,
                            ),
                        )
                    ),
                ),
            )
        }
    },

    Axiom9(
        Expression.parseClassical(
            "(A -> B) -> (A -> !B) -> !A"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val h = hypothesis +
                Expression.Imp(a, b) +
                Expression.Imp(a, Expression.Imp(b, Expression.Bottom)) +
                a

            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    Expression.Imp(a, b),
                    Expression.Imp(
                        Expression.Imp(
                            a,
                            Expression.Imp(b, Expression.Bottom),
                        ),
                        Expression.Imp(a, Expression.Bottom),
                    ),
                ),
                level,
                NaturalProof.InsertImp(
                    h.dropLast(2),
                    Expression.Imp(
                        Expression.Imp(
                            a,
                            Expression.Imp(b, Expression.Bottom),
                        ),
                        Expression.Imp(a, Expression.Bottom),
                    ),
                    level + 1,
                    NaturalProof.InsertImp(
                        h.dropLast(1),
                        Expression.Imp(a, Expression.Bottom),
                        level + 2,
                        NaturalProof.EraseImp(
                            h,
                            Expression.Bottom,
                            level + 3,
                            NaturalProof.EraseImp(
                                h,
                                Expression.Imp(b, Expression.Bottom),
                                level + 4,
                                NaturalProof.Ax(
                                    h,
                                    Expression.Imp(a, Expression.Imp(b, Expression.Bottom)),
                                    level + 5,
                                ),
                                NaturalProof.Ax(
                                    h,
                                    a,
                                    level + 5,
                                ),
                            ),
                            NaturalProof.EraseImp(
                                h,
                                b,
                                level + 4,
                                NaturalProof.Ax(
                                    h,
                                    Expression.Imp(a, b),
                                    level + 5,
                                ),
                                NaturalProof.Ax(
                                    h,
                                    a,
                                    level + 5,
                                ),
                            ),
                        )
                    )
                )
            )
        }
    },

    Axiom10(
        Expression.parseClassical(
            "A -> !A -> B"
        ).replaceVars(),
    ) {
        override fun naturalProof(
            expression: Expression,
            hypothesis: List<Expression>,
            level: Int,
        ): NaturalProof {
            val map = matchesScheme(expression, this.scheme)
                ?: throw IllegalArgumentException("Passed expression is not an axiom")
            val a = map.getValue("a")
            val b = map.getValue("b")
            val h = hypothesis + a + Expression.Imp(a, Expression.Bottom)

            return NaturalProof.InsertImp(
                hypothesis,
                Expression.Imp(
                    a,
                    Expression.Imp(
                        Expression.Imp(a, Expression.Bottom),
                        b,
                    ),
                ),
                level,
                NaturalProof.InsertImp(
                    h.dropLast(1),
                    Expression.Imp(
                        Expression.Imp(a, Expression.Bottom),
                        b,
                    ),
                    level + 1,
                    NaturalProof.EraseBottom(
                        h,
                        b,
                        level + 2,
                        NaturalProof.EraseImp(
                            h,
                            Expression.Bottom,
                            level + 3,
                            NaturalProof.Ax(
                                h,
                                Expression.Imp(a, Expression.Bottom),
                                level + 4,
                            ),
                            NaturalProof.Ax(
                                h,
                                a,
                                level + 4,
                            ),
                        )
                    )
                )
            )
        }
    },
    ;

    abstract fun naturalProof(
        expression: Expression,
        hypothesis: List<Expression>,
        level: Int,
    ): NaturalProof

    companion object {

        fun Expression.isAxiom(): Axiom? {
            for (value in values()) {
                if (matchesScheme(this, value.scheme) != null) {
                    return value
                }
            }
            return null
        }

        private fun matchesScheme(
            expression: Expression,
            axiomScheme: Expression,
        ): Map<String, Expression>? {
            if (axiomScheme is Expression.Var) {
                return mapOf(axiomScheme.name to expression)
            }
            if (expression is Expression.Not && axiomScheme is Expression.Not) {
                return matchesScheme(expression.arg, axiomScheme.arg)
            } else if (expression is Expression.And && axiomScheme is Expression.And) {
                return processMaps(
                    matchesScheme(expression.left, axiomScheme.left),
                    matchesScheme(expression.right, axiomScheme.right),
                )
            } else if (expression is Expression.Or && axiomScheme is Expression.Or) {
                return processMaps(
                    matchesScheme(expression.left, axiomScheme.left),
                    matchesScheme(expression.right, axiomScheme.right),
                )
            } else if (expression is Expression.Imp && axiomScheme is Expression.Imp) {
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
    }
}

private fun Expression.replaceVars(): Expression {

    return when (this) {
        is Expression.Var -> this.copy(name = this.name.lowercase(Locale.ENGLISH))
        is Expression.Not -> this.copy(arg = this.arg.replaceVars())
        is Expression.And -> this.copy(
            left = this.left.replaceVars(),
            right = this.right.replaceVars(),
        )
        is Expression.Or -> this.copy(
            left = this.left.replaceVars(),
            right = this.right.replaceVars(),
        )
        is Expression.Imp -> this.copy(
            left = this.left.replaceVars(),
            right = this.right.replaceVars(),
        )
        is Expression.Bottom -> this
    }
}
