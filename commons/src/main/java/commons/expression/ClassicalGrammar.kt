package commons.expression

import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.rightAssociative
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.combinators.use
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

internal object ClassicalGrammar : Grammar<Expression>() {
    private val id by regexToken("[A-Z]([A-Z0-9’'])*")
    private val not by regexToken("!")
    private val and by regexToken("&")
    private val or by regexToken("\\|")
    private val imp by regexToken("->")
    private val leftPar by regexToken("\\(")
    private val rightPar by regexToken("\\)")

    private val term: Parser<Expression> by
    (id use { Expression.Var(text) }) or
        (-not * parser(this::term) map { Expression.Not(it) }) or
        (-leftPar * parser(this::rootParser) * -rightPar)

    private val andChain by leftAssociative(term, and) { l, _, r -> Expression.And(l, r) }
    private val orChain by leftAssociative(andChain, or) { l, _, r -> Expression.Or(l, r) }

    override val rootParser by rightAssociative(orChain, imp) { l, _, r -> Expression.Imp(l, r) }
}
