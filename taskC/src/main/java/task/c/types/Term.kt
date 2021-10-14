package task.c.types

sealed interface Term {

    val infixString: String

    fun entersFree(variable: Var): Boolean
    fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var>
    fun replaceFree(variable: Var, term: Term): Term
    fun extractFreeReplacements(replaced: Term, variable: Var): Set<Term>?
    fun extractVariables(): Set<Var>
}

sealed interface BinaryTerm : Term {
    val left: Term
    val right: Term

    override fun entersFree(variable: Var): Boolean {
        return left.entersFree(variable) || right.entersFree(variable)
    }

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> {
        return left.extractFreeQuantifiers(variable, under) +
            right.extractFreeQuantifiers(variable, under)
    }

    override fun extractVariables(): Set<Var> {
        return left.extractVariables() + right.extractVariables()
    }
}

object Zero : Term {
    override val infixString: String = "0"

    override fun entersFree(variable: Var): Boolean = false

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> = emptySet()

    override fun replaceFree(variable: Var, term: Term): Zero = Zero

    override fun extractFreeReplacements(replaced: Term, variable: Var): Set<Term>? {
        return if (replaced is Zero) {
            emptySet()
        } else {
            null
        }
    }

    override fun extractVariables(): Set<Var> = emptySet()
}

data class Var(
    val name: String
) : Term {
    override val infixString: String = name

    override fun entersFree(variable: Var): Boolean {
        return this == variable
    }

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> {
        return if (this == variable) {
            under
        } else {
            emptySet()
        }
    }

    override fun replaceFree(variable: Var, term: Term): Term {
        return if (this == variable) {
            term
        } else {
            this
        }
    }

    override fun extractFreeReplacements(replaced: Term, variable: Var): Set<Term>? {
        if (this == variable) {
            return setOf(replaced)
        }
        return if (replaced == this) {
            emptySet()
        } else {
            null
        }
    }

    override fun extractVariables(): Set<Var> = setOf(this)
}

data class Inc(
    val arg: Term,
) : Term {
    override val infixString: String get() = "${arg.infixString}'"

    override fun entersFree(variable: Var): Boolean {
        return arg.entersFree(variable)
    }

    override fun extractFreeQuantifiers(variable: Var, under: Set<Var>): Set<Var> {
        return arg.extractFreeQuantifiers(variable, under)
    }

    override fun replaceFree(variable: Var, term: Term): Inc {
        return Inc(arg.replaceFree(variable, term))
    }

    override fun extractFreeReplacements(replaced: Term, variable: Var): Set<Term>? {
        return if (replaced is Inc) {
            arg.extractFreeReplacements(replaced.arg, variable)
        } else {
            null
        }
    }

    override fun extractVariables(): Set<Var> = arg.extractVariables()
}

data class Add(
    override val left: Term,
    override val right: Term,
) : BinaryTerm {
    override val infixString: String get() = "(${left.infixString}+${right.infixString})"

    override fun replaceFree(variable: Var, term: Term): Add {
        return Add(
            left.replaceFree(variable, term),
            right.replaceFree(variable, term),
        )
    }

    override fun extractFreeReplacements(replaced: Term, variable: Var): Set<Term>? {
        return if (replaced is Add) {
            (left.extractFreeReplacements(replaced.left, variable) ?: return null) +
                (right.extractFreeReplacements(replaced.right, variable) ?: return null)
        } else {
            null
        }
    }
}

data class Mul(
    override val left: Term,
    override val right: Term,
) : BinaryTerm {
    override val infixString: String get() = "(${left.infixString}*${right.infixString})"

    override fun replaceFree(variable: Var, term: Term): Mul {
        return Mul(
            left.replaceFree(variable, term),
            right.replaceFree(variable, term),
        )
    }

    override fun extractFreeReplacements(replaced: Term, variable: Var): Set<Term>? {
        return if (replaced is Mul) {
            (left.extractFreeReplacements(replaced.left, variable) ?: return null) +
                (right.extractFreeReplacements(replaced.right, variable) ?: return null)
        } else {
            null
        }
    }
}
