package commons.expression

import com.github.h0tk3y.betterParse.grammar.parseToEnd

sealed interface Expression {

    fun classicalString(): String
    fun naturalString(): String
    fun mathString(): String

    data class Var(
        val name: String,
    ) : Expression {
        override fun classicalString() = name
        override fun naturalString() = name
        override fun mathString() = name
    }

    data class Not(
        val arg: Expression,
    ) : Expression {
        override fun classicalString() = "(!${arg.classicalString()})"
        override fun naturalString() = "(${arg.naturalString()}->_|_)"
        override fun mathString() = "(!${arg.mathString()})"
    }

    data class And(
        val left: Expression,
        val right: Expression,
    ) : Expression {
        override fun classicalString() = "(${left.classicalString()}&${right.classicalString()})"
        override fun naturalString() = "(${left.naturalString()}&${right.naturalString()})"
        override fun mathString() = "(&,${left.mathString()},${right.mathString()})"
    }

    data class Or(
        val left: Expression,
        val right: Expression,
    ) : Expression {
        override fun classicalString() = "(${left.classicalString()}|${right.classicalString()})"
        override fun naturalString() = "(${left.naturalString()}|${right.naturalString()})"
        override fun mathString() = "(|,${left.mathString()},${right.mathString()})"
    }

    data class Imp(
        val left: Expression,
        val right: Expression,
    ) : Expression {
        override fun classicalString() = "(${left.classicalString()}->${right.classicalString()})"
        override fun naturalString() = "(${left.naturalString()}->${right.naturalString()})"
        override fun mathString() = "(->,${left.mathString()},${right.mathString()})"
    }

    object Bottom : Expression {
        override fun classicalString() = "_|_"
        override fun naturalString() = "_|_"
        override fun mathString() = "_|_"
    }

    companion object {

        fun parseClassical(string: String): Expression =
            string.filterNot(Char::isWhitespace)
                .let(ClassicalGrammar::parseToEnd)
    }
}
