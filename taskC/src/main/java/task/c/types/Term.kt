package task.c.types

sealed interface Term {

    val infixString: String

    fun replaceFree(oldVar: Var, replacement: Term): Term
    fun extractTerms(target: Term, variable: Var): Set<Term>?
    fun extractVariables(): Set<Var>
}

sealed interface BinaryTerm : Term {
    val left: Term
    val right: Term

    override fun extractVariables(): Set<Var> {
        return left.extractVariables() + right.extractVariables()
    }
}

object Zero : Term {
    override val infixString: String = "0"

    override fun replaceFree(oldVar: Var, replacement: Term): Zero = Zero

    override fun extractTerms(target: Term, variable: Var): Set<Term> = emptySet()

    override fun extractVariables(): Set<Var> = emptySet()
}

data class Var(
    val name: String
) : Term {
    override val infixString: String = name

    override fun replaceFree(oldVar: Var, replacement: Term): Term {
        return if (name == oldVar.name) {
            replacement
        } else {
            this
        }
    }

    override fun extractTerms(target: Term, variable: Var): Set<Term> {
        return if (variable.name == name) {
            setOf(target)
        } else {
            emptySet()
        }
    }

    override fun extractVariables(): Set<Var> = setOf(this)
}

data class Inc(
    val arg: Term,
) : Term {
    override val infixString: String = "(${arg.infixString})"

    override fun replaceFree(oldVar: Var, replacement: Term): Inc {
        return Inc(arg.replaceFree(oldVar, replacement))
    }

    override fun extractTerms(target: Term, variable: Var): Set<Term>? {
        return if (target !is Inc) {
            null
        } else {
            arg.extractTerms(target.arg, variable)
        }
    }

    override fun extractVariables(): Set<Var> = arg.extractVariables()
}

data class Add(
    override val left: Term,
    override val right: Term,
) : BinaryTerm {
    override val infixString: String = "(${left.infixString}+${right.infixString})"

    override fun replaceFree(oldVar: Var, replacement: Term): Add {
        return Add(
            left.replaceFree(oldVar, replacement),
            right.replaceFree(oldVar, replacement),
        )
    }

    override fun extractTerms(target: Term, variable: Var): Set<Term>? {
        return if (target !is Add) {
            null
        } else {
            val leftSet = left.extractTerms(target.left, variable) ?: return null
            val rightSet = right.extractTerms(target.right, variable) ?: return null
            leftSet + rightSet
        }
    }
}

data class Mul(
    override val left: Term,
    override val right: Term,
) : BinaryTerm {
    override val infixString: String = "(${left.infixString}*${right.infixString})"

    override fun replaceFree(oldVar: Var, replacement: Term): Mul {
        return Mul(
            left.replaceFree(oldVar, replacement),
            right.replaceFree(oldVar, replacement),
        )
    }

    override fun extractTerms(target: Term, variable: Var): Set<Term>? {
        return if (target !is Mul) {
            null
        } else {
            val leftSet = left.extractTerms(target.left, variable) ?: return null
            val rightSet = right.extractTerms(target.right, variable) ?: return null
            leftSet + rightSet
        }
    }
}
