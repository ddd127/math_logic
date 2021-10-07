package task.c.types

import com.github.h0tk3y.betterParse.grammar.parseToEnd

sealed interface Expression {

    val infixString: String

    fun isFree(variable: Var): Boolean
    fun replaceFree(oldVar: Var, replacement: Term): Expression
    fun extractTerms(target: Expression, variable: Var): Set<Term>?
    fun extractVariables(): Set<Var>
    fun isFreeForReplace(target: Var, replacement: Term): Boolean

    companion object {
        fun parseExpression(string: String): Expression =
            string.filterNot(Char::isWhitespace)
                .let(Grammar::parseToEnd)
    }
}

sealed interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression

    override fun isFree(variable: Var): Boolean {
        return left.isFree(variable) && right.isFree(variable)
    }

    override fun extractVariables(): Set<Var> {
        return left.extractVariables() + right.extractVariables()
    }

    override fun isFreeForReplace(target: Var, replacement: Term): Boolean {
        return left.isFreeForReplace(target, replacement) && right.isFreeForReplace(target, replacement)
    }
}

sealed interface QuantifierExpression : Expression {
    val variable: Var
    val expression: Expression

    override fun isFree(variable: Var): Boolean {
        return (variable != this.variable) && expression.isFree(variable)
    }

    override fun extractVariables(): Set<Var> {
        return expression.extractVariables() + variable
    }

    override fun isFreeForReplace(target: Var, replacement: Term): Boolean {
        return if (variable == target || !expression.isFreeForReplace(target, replacement)) {
            false
        } else {
            !replacement.extractVariables().contains(variable)
        }
    }
}

data class Predicate(
    val name: String,
) : Expression {
    override val infixString: String = name

    override fun isFree(variable: Var) = true

    override fun replaceFree(oldVar: Var, replacement: Term): Predicate {
        return this
    }

    override fun extractTerms(target: Expression, variable: Var): Set<Term> = emptySet()

    override fun extractVariables(): Set<Var> = emptySet()

    override fun isFreeForReplace(target: Var, replacement: Term): Boolean = true
}

data class Eq(
    val left: Term,
    val right: Term,
) : Expression {
    override val infixString: String = "(${left.infixString}=${right.infixString})"

    override fun isFree(variable: Var): Boolean = true

    override fun replaceFree(oldVar: Var, replacement: Term): Eq {
        return Eq(
            left.replaceFree(oldVar, replacement),
            right.replaceFree(oldVar, replacement),
        )
    }

    override fun extractTerms(target: Expression, variable: Var): Set<Term>? {
        return if (target !is Eq) {
            null
        } else {
            val leftSet = left.extractTerms(target.left, variable) ?: return null
            val rightSet = right.extractTerms(target.right, variable) ?: return null
            leftSet + rightSet
        }
    }

    override fun extractVariables(): Set<Var> {
        return left.extractVariables() + right.extractVariables()
    }

    override fun isFreeForReplace(target: Var, replacement: Term): Boolean = true
}

data class Not(
    val arg: Expression,
) : Expression {
    override val infixString: String = "(!${arg.infixString})"

    override fun isFree(variable: Var): Boolean {
        return arg.isFree(variable)
    }

    override fun replaceFree(oldVar: Var, replacement: Term): Not {
        return Not(arg.replaceFree(oldVar, replacement))
    }

    override fun extractTerms(target: Expression, variable: Var): Set<Term>? {
        return if (target !is Not) {
            null
        } else {
            arg.extractTerms(target.arg, variable)
        }
    }

    override fun extractVariables(): Set<Var> {
        return arg.extractVariables()
    }

    override fun isFreeForReplace(target: Var, replacement: Term): Boolean {
        return arg.isFreeForReplace(target, replacement)
    }
}

data class Imp(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String = "(${left.infixString}->${right.infixString})"

    override fun replaceFree(oldVar: Var, replacement: Term): Imp {
        return Imp(
            left.replaceFree(oldVar, replacement),
            right.replaceFree(oldVar, replacement),
        )
    }

    override fun extractTerms(target: Expression, variable: Var): Set<Term>? {
        return if (target !is Imp) {
            null
        } else {
            val leftSet = left.extractTerms(target.left, variable) ?: return null
            val rightSet = right.extractTerms(target.right, variable) ?: return null
            leftSet + rightSet
        }
    }
}

data class And(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String = "(${left.infixString}&${right.infixString})"

    override fun replaceFree(oldVar: Var, replacement: Term): And {
        return And(
            left.replaceFree(oldVar, replacement),
            right.replaceFree(oldVar, replacement),
        )
    }

    override fun extractTerms(target: Expression, variable: Var): Set<Term>? {
        return if (target !is And) {
            null
        } else {
            val leftSet = left.extractTerms(target.left, variable) ?: return null
            val rightSet = right.extractTerms(target.right, variable) ?: return null
            leftSet + rightSet
        }
    }
}

data class Or(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String = "(${left.infixString}|${right.infixString})"

    override fun replaceFree(oldVar: Var, replacement: Term): Or {
        return Or(
            left.replaceFree(oldVar, replacement),
            right.replaceFree(oldVar, replacement),
        )
    }

    override fun extractTerms(target: Expression, variable: Var): Set<Term>? {
        return if (target !is Or) {
            null
        } else {
            val leftSet = left.extractTerms(target.left, variable) ?: return null
            val rightSet = right.extractTerms(target.right, variable) ?: return null
            leftSet + rightSet
        }
    }
}

data class ForAny(
    override val variable: Var,
    override val expression: Expression,
) : QuantifierExpression {
    override val infixString: String = "(@${variable.infixString}.${expression.infixString})"

    override fun replaceFree(oldVar: Var, replacement: Term): ForAny {
        if (variable == oldVar) {
            throw IllegalStateException("Can not replace not free variable")
        }
        return ForAny(
            variable,
            expression.replaceFree(oldVar, replacement),
        )
    }

    override fun extractTerms(target: Expression, variable: Var): Set<Term>? {
        return if (target !is ForAny) {
            null
        } else {
            val leftSet = this.variable.extractTerms(target.variable, variable)
            val rightSet = expression.extractTerms(target.expression, variable) ?: return null
            leftSet + rightSet
        }
    }
}

data class Exists(
    override val variable: Var,
    override val expression: Expression,
) : QuantifierExpression {
    override val infixString: String = "(?${variable.infixString}.${expression.infixString})"

    override fun replaceFree(oldVar: Var, replacement: Term): Exists {
        if (variable == oldVar) {
            throw IllegalStateException("Can not replace not free variable")
        }
        return Exists(
            variable,
            expression.replaceFree(oldVar, replacement),
        )
    }

    override fun extractTerms(target: Expression, variable: Var): Set<Term>? {
        return if (target !is Exists) {
            null
        } else {
            val leftSet = this.variable.extractTerms(target.variable, variable)
            val rightSet = expression.extractTerms(target.expression, variable) ?: return null
            leftSet + rightSet
        }
    }
}
