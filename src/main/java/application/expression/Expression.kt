package application.expression

import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.rightAssociative
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

sealed interface Expression {

    fun toMathString(): String

    data class Var(val name: String) : Expression {
        override fun toMathString(): String = name
    }

    data class Not(val exp: Expression) : Expression {
        override fun toMathString(): String =
            "(!${exp.toMathString()})"
    }

    data class And(val left: Expression, val right: Expression) : Expression {
        override fun toMathString(): String =
            "(&,${left.toMathString()},${right.toMathString()})"
    }

    data class Or(val left: Expression, val right: Expression) : Expression {
        override fun toMathString(): String =
            "(|,${left.toMathString()},${right.toMathString()})"
    }

    data class Imp(val left: Expression, val right: Expression) : Expression {
        override fun toMathString(): String =
            "(->,${left.toMathString()},${right.toMathString()})"
    }
}

val grammar = object : Grammar<Expression>() {

    val id by regexToken("[A-Z]([A-Z0-9’'])*")
    val not by regexToken("!")
    val and by regexToken("&")
    val or by regexToken("\\|")
    val imp by regexToken("->")
    val leftPar by regexToken("\\(")
    val rightPar by regexToken("\\)")

    val term: Parser<Expression> by
    (id use { Expression.Var(text) }) or
        (-not * parser(this::term) map { Expression.Not(it) }) or
        (-leftPar * parser(this::rootParser) * -rightPar)

    val andChain by leftAssociative(term, and) { l, _, r -> Expression.And(l, r) }
    val orChain by leftAssociative(andChain, or) { l, _, r -> Expression.Or(l, r) }
    override val rootParser by rightAssociative(orChain, imp) { l, _, r -> Expression.Imp(l, r) }
}
