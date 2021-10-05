package task.c.types

import com.github.h0tk3y.betterParse.grammar.parseToEnd

sealed interface Expression {

    val infixString: String

    companion object {
        fun parseExpression(string: String): Expression =
            string.filterNot(Char::isWhitespace)
                .let(Grammar::parseToEnd)
    }
}

sealed interface BinaryExpression : Expression {
    val left: Expression
    val right: Expression
}

sealed interface QuantifierExpression : Expression {
    val left: Var
    val right: Expression
}

data class Predicate(
    val name: String,
) : Expression {
    override val infixString: String = name
}

data class Eq(
    val left: Term,
    val right: Term,
) : Expression {
    override val infixString: String = "(${left.infixString}=${right.infixString})"
}

data class Not(
    val arg: Expression,
) : Expression {
    override val infixString: String = "(${arg.infixString})"
}

data class Imp(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String = "(${left.infixString}->${right.infixString})"
}

data class And(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String = "(${left.infixString}&${right.infixString})"
}

data class Or(
    override val left: Expression,
    override val right: Expression,
) : BinaryExpression {
    override val infixString: String = "(${left.infixString}|${right.infixString})"
}

data class ForAny(
    override val left: Var,
    override val right: Expression,
) : QuantifierExpression {
    override val infixString: String = "@${left.infixString}.${right.infixString}"
}

data class Exists(
    override val left: Var,
    override val right: Expression,
) : QuantifierExpression {
    override val infixString: String = "?${left.infixString}.${right.infixString}"
}
