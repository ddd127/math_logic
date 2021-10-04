package commons.expression

import com.github.h0tk3y.betterParse.grammar.parseToEnd

sealed interface Expression {

    val classicalString: String
    val naturalString: String
    val mathString: String

    data class Var(
        val name: String,
    ) : Expression {
        override val classicalString = name
        override val naturalString = name
        override val mathString = name
    }

    data class Not(
        val arg: Expression,
    ) : Expression {
        override val classicalString = "(!${arg.classicalString})"
        override val naturalString = "(${arg.naturalString}->_|_)"
        override val mathString = "(!${arg.mathString})"
    }

    data class And(
        val left: Expression,
        val right: Expression,
    ) : Expression {
        override val classicalString = "(${left.classicalString}&${right.classicalString})"
        override val naturalString = "(${left.naturalString}&${right.naturalString})"
        override val mathString = "(&,${left.mathString},${right.mathString})"
    }

    data class Or(
        val left: Expression,
        val right: Expression,
    ) : Expression {
        override val classicalString = "(${left.classicalString}|${right.classicalString})"
        override val naturalString = "(${left.naturalString}|${right.naturalString})"
        override val mathString = "(|,${left.mathString},${right.mathString})"
    }

    data class Imp(
        val left: Expression,
        val right: Expression,
    ) : Expression {
        override val classicalString = "(${left.classicalString}->${right.classicalString})"
        override val naturalString = "(${left.naturalString}->${right.naturalString})"
        override val mathString = "(->,${left.mathString},${right.mathString})"
    }

    object Bottom : Expression {
        override val classicalString: String = "_|_"
        override val naturalString: String = "_|_"
        override val mathString: String = "_|_"
    }

    companion object {

        fun parseClassical(string: String): Expression =
            string.filterNot(Char::isWhitespace)
                .let(ClassicalGrammar::parseToEnd)
    }
}
