package task.c.types

sealed interface Term {
    val infixString: String
}

sealed interface BinaryTerm : Term {
    val left: Term
    val right: Term
}

object Zero : Term {
    override val infixString: String = "0"
}

data class Var(
    val name: String
) : Term {
    override val infixString: String = name
}

data class Inc(
    val arg: Term,
) : Term {
    override val infixString: String = "(${arg.infixString})"
}

data class Add(
    override val left: Term,
    override val right: Term,
) : BinaryTerm {
    override val infixString: String = "(${left.infixString}+${right.infixString})"
}

data class Mul(
    override val left: Term,
    override val right: Term,
) : BinaryTerm {
    override val infixString: String = "(${left.infixString}*${right.infixString})"
}
