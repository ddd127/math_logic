package task.c.types

import com.github.h0tk3y.betterParse.grammar.parseToEnd

sealed interface Expression {

    val infixString: String

    fun entersFree(variable: Var): Boolean
    fun extractFreeQuantifiers(variable: Var, under: Set<Var> = emptySet()): Set<Var>
    fun replaceFree(variable: Var, term: Term): Expression
    fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>?

    companion object {
        fun parseExpression(string: String): Expression =
            string.filterNot(Char::isWhitespace)
                .let(Grammar::parseToEnd)
    }
}

sealed interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression

    override fun entersFree(variable: Var): Boolean {
        return left.entersFree(variable) || right.entersFree(variable)
    }

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> {
        return left.extractFreeQuantifiers(variable, under) +
            right.extractFreeQuantifiers(variable, under)
    }
}

sealed interface QuantifierExpression : Expression {
    val variable: Var
    val expression: Expression

    override fun entersFree(variable: Var): Boolean {
        return if (this.variable == variable) {
            false
        } else {
            expression.entersFree(variable)
        }
    }

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> {
        return if (this.variable == variable) {
            emptySet()
        } else {
            expression.extractFreeQuantifiers(
                variable,
                under + this.variable,
            )
        }
    }
}

data class Predicate(
    val name: String,
) : Expression {
    override val infixString: String = name

    override fun entersFree(variable: Var): Boolean = false

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> = emptySet()

    override fun replaceFree(variable: Var, term: Term): Predicate = this

    override fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>? {
        return if (this == replaced) {
            emptySet()
        } else {
            null
        }
    }
}

data class Eq(
    val left: Term,
    val right: Term,
) : Expression {
    override val infixString: String get() = "(${left.infixString}=${right.infixString})"

    override fun entersFree(variable: Var): Boolean {
        return left.entersFree(variable) || right.entersFree(variable)
    }

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> {
        return left.extractFreeQuantifiers(variable, under) +
            right.extractFreeQuantifiers(variable, under)
    }

    override fun replaceFree(variable: Var, term: Term): Eq {
        return Eq(
            left.replaceFree(variable, term),
            right.replaceFree(variable, term),
        )
    }

    override fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>? {
        return if (replaced is Eq) {
            (left.extractFreeReplacements(replaced.left, variable) ?: return null) +
                (right.extractFreeReplacements(replaced.right, variable) ?: return null)
        } else {
            null
        }
    }
}

data class Not(
    val arg: Expression,
) : Expression {
    override val infixString: String get() = "(!${arg.infixString})"

    override fun entersFree(variable: Var): Boolean {
        return arg.entersFree(variable)
    }

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> {
        return arg.extractFreeQuantifiers(variable, under)
    }

    override fun replaceFree(variable: Var, term: Term): Not {
        return Not(arg.replaceFree(variable, term))
    }

    override fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>? {
        return if (replaced is Not) {
            arg.extractFreeReplacements(replaced.arg, variable)
        } else {
            null
        }
    }
}

data class Imp(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String get() = "(${left.infixString}->${right.infixString})"

    override fun replaceFree(variable: Var, term: Term): Imp {
        return Imp(
            left.replaceFree(variable, term),
            right.replaceFree(variable, term),
        )
    }

    override fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>? {
        return if (replaced is Imp) {
            (left.extractFreeReplacements(replaced.left, variable) ?: return null) +
                (right.extractFreeReplacements(replaced.right, variable) ?: return null)
        } else {
            null
        }
    }
}

data class And(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String get() = "(${left.infixString}&${right.infixString})"

    override fun replaceFree(variable: Var, term: Term): And {
        return And(
            left.replaceFree(variable, term),
            right.replaceFree(variable, term),
        )
    }

    override fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>? {
        return if (replaced is And) {
            (left.extractFreeReplacements(replaced.left, variable) ?: return null) +
                (right.extractFreeReplacements(replaced.right, variable) ?: return null)
        } else {
            null
        }
    }
}

data class Or(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String get() = "(${left.infixString}|${right.infixString})"

    override fun replaceFree(variable: Var, term: Term): Or {
        return Or(
            left.replaceFree(variable, term),
            right.replaceFree(variable, term),
        )
    }

    override fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>? {
        return if (replaced is Or) {
            (left.extractFreeReplacements(replaced.left, variable) ?: return null) +
                (right.extractFreeReplacements(replaced.right, variable) ?: return null)
        } else {
            null
        }
    }
}

data class ForAny(
    override val variable: Var,
    override val expression: Expression,
) : QuantifierExpression {
    override val infixString: String get() = "(@${variable.infixString}.${expression.infixString})"

    override fun replaceFree(variable: Var, term: Term): ForAny {
        return if (this.variable == variable) {
            this
        } else {
            ForAny(
                this.variable,
                expression.replaceFree(variable, term),
            )
        }
    }

    override fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>? {
        if (replaced !is ForAny) {
            return null
        }
        if (this.variable != variable) {
            return (this.variable.extractFreeReplacements(replaced.variable, variable) ?: return null) +
                (expression.extractFreeReplacements(replaced.expression, variable) ?: return null)
        }
        return if (this == replaced) {
            emptySet()
        } else {
            null
        }
    }
}

data class Exists(
    override val variable: Var,
    override val expression: Expression,
) : QuantifierExpression {
    override val infixString: String get() = "(?${variable.infixString}.${expression.infixString})"

    override fun replaceFree(variable: Var, term: Term): Exists {
        return if (this.variable == variable) {
            this
        } else {
            Exists(
                this.variable,
                expression.replaceFree(variable, term),
            )
        }
    }

    override fun extractFreeReplacements(replaced: Expression, variable: Var): Set<Term>? {
        if (replaced !is Exists) {
            return null
        }
        if (this.variable != variable) {
            return (this.variable.extractFreeReplacements(replaced.variable, variable) ?: return null) +
                (expression.extractFreeReplacements(replaced.expression, variable) ?: return null)
        }
        return if (this == replaced) {
            emptySet()
        } else {
            null
        }
    }
}
