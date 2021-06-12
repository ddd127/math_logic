package application.second.expression

import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.rightAssociative
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

sealed interface Expression {

    fun toMathString(): String

    fun isAxiom(): Boolean

    data class FalseTerm(val fiction: String) : Expression {

        override fun toMathString(): String = "_|_"

        override fun isAxiom(): Boolean = false
    }

    data class Var(val name: String) : Expression {

        override fun toMathString(): String = name

        override fun isAxiom(): Boolean = false
    }

    data class And(val left: Expression, val right: Expression) : Expression {

        override fun toMathString(): String =
            "(&,${left.toMathString()},${right.toMathString()})"

        override fun isAxiom(): Boolean = false
    }

    data class Or(val left: Expression, val right: Expression) : Expression {

        override fun toMathString(): String =
            "(|,${left.toMathString()},${right.toMathString()})"

        override fun isAxiom(): Boolean = false
    }

    data class Imp(val left: Expression, val right: Expression) : Expression {

        override fun toMathString(): String =
            "(->,${left.toMathString()},${right.toMathString()})"

        override fun isAxiom(): Boolean =
            ((right is Imp) && (left == right.right)) ||
                ((left is Imp) &&
                    (right is Imp) &&
                    (right.left is Imp) &&
                    (right.right is Imp) &&
                    (right.left.right is Imp) &&
                    (left.left == right.left.left) &&
                    (left.left == right.right.left) &&
                    (left.right == right.left.right.left) &&
                    (right.left.right.right == right.right.right)) ||
                ((right is Imp) &&
                    (right.right is And) &&
                    (left == right.right.left) &&
                    (right.left == right.right.right)) ||
                ((left is And) && (left.left == right)) ||
                ((left is And) && (left.right == right)) ||
                ((right is Or) && (left == right.left)) ||
                ((right is Or) && (left == right.right)) ||
                ((left is Imp) &&
                    (right is Imp) &&
                    (right.left is Imp) &&
                    (right.right is Imp) &&
                    (right.right.left is Or) &&
                    (left.left == right.right.left.left) &&
                    (left.right == right.left.right) &&
                    (right.left.right == right.right.right) &&
                    (right.left.left == right.right.left.right)) ||
                ((left is Imp) &&
                    (right is Imp) &&
                    (right.left is Imp) &&
                    (right.right is Imp) &&
                    (right.right.right is FalseTerm) &&
                    (right.left.right is Imp) &&
                    (right.left.right.right is FalseTerm) &&
                    (left.left == right.left.left) &&
                    (left.left == right.right.left) &&
                    (left.right == right.left.right.left)) ||
                ((right is Imp) &&
                    (right.left is Imp) &&
                    (right.left.right is FalseTerm) &&
                    (left == right.left.left))
    }
}

val grammar = object : Grammar<Expression>() {

    val falseTerm by regexToken("_\\|_")
    val id by regexToken("[A-Z]([A-Z0-9’'])*")
    val and by regexToken("&")
    val or by regexToken("\\|")
    val imp by regexToken("->")
    val leftPar by regexToken("\\(")
    val rightPar by regexToken("\\)")

    val term: Parser<Expression> by
    (id use { Expression.Var(text) }) or
        (falseTerm use { Expression.FalseTerm("_|_") }) or
        (-leftPar * parser(this::rootParser) * -rightPar)

    val andChain by leftAssociative(term, and) { l, _, r -> Expression.And(l, r) }
    val orChain by leftAssociative(andChain, or) { l, _, r -> Expression.Or(l, r) }
    override val rootParser by rightAssociative(orChain, imp) { l, _, r -> Expression.Imp(l, r) }
}
