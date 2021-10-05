package task.c.types

import com.github.h0tk3y.betterParse.combinators.asJust
import com.github.h0tk3y.betterParse.combinators.leftAssociative
import com.github.h0tk3y.betterParse.combinators.map
import com.github.h0tk3y.betterParse.combinators.or
import com.github.h0tk3y.betterParse.combinators.rightAssociative
import com.github.h0tk3y.betterParse.combinators.times
import com.github.h0tk3y.betterParse.combinators.unaryMinus
import com.github.h0tk3y.betterParse.combinators.zeroOrMore
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

internal object Grammar : Grammar<Expression>() {

    private val leftParLiteral by literalToken("(")
    private val rightParLiteral by literalToken(")")

    private val zeroLiteral by literalToken("0")
    private val variableLiteral by regexToken("[a-z]")
    private val addLiteral by literalToken("+")
    private val mulLiteral by literalToken("*")
    private val incLiteral by literalToken("'")

    private val predicateLiteral by regexToken("[A-Z]")

    private val pointLiteral by literalToken(".")
    private val forAnyLiteral by literalToken("@")
    private val existsLiteral by literalToken("?")

    private val eqLiteral by literalToken("=")

    private val notLiteral by literalToken("!")
    private val andLiteral by literalToken("&")
    private val orLiteral by literalToken("|")
    private val impLiteral by literalToken("->")

    private val zero: Parser<Term> by zeroLiteral asJust Zero
    private val variable: Parser<Var> by variableLiteral map { Var(it.text) }

    private val parTerm: Parser<Term> by
    -leftParLiteral * parser(::addChain) * -rightParLiteral

    private val incArg: Parser<Term> by
    parTerm or zero or variable

    private val inc: Parser<Term> by
    incArg * zeroOrMore(incLiteral) map {
        it.t2.fold(it.t1) { a, _ -> Inc(a) }
    }

    private val term: Parser<Term> by
    inc or incArg

    private val mulChain: Parser<Term> by
    leftAssociative(term, mulLiteral) { l, _, r ->
        Mul(l, r)
    }

    private val addChain: Parser<Term> by
    leftAssociative(mulChain, addLiteral) { l, _, r ->
        Add(l, r)
    }

    private val eq: Parser<Expression> by
    addChain * -eqLiteral * addChain map { Eq(it.t1, it.t2) }

    private val predicate: Parser<Expression> by
    predicateLiteral map { Predicate(it.text) } or eq

    private val forAny: Parser<Expression> by
    -forAnyLiteral * parser(::variable) * -pointLiteral * parser(::impChain) map {
        ForAny(it.t1, it.t2)
    }

    private val exists: Parser<Expression> by
    -existsLiteral * parser(::variable) * -pointLiteral * parser(::impChain) map {
        Exists(it.t1, it.t2)
    }

    private val parExpression: Parser<Expression> by
    -leftParLiteral * parser(::impChain) * -rightParLiteral

    private val not: Parser<Expression> by
    -notLiteral * parser(::unary) map { Not(it) }

    private val unary: Parser<Expression> by
    not or parExpression or predicate or forAny or exists

    private val andChain: Parser<Expression> by
    leftAssociative(unary, andLiteral) { l, _, r ->
        And(l, r)
    }

    private val orChain: Parser<Expression> by
    leftAssociative(andChain, orLiteral) { l, _, r ->
        Or(l, r)
    }

    private val impChain: Parser<Expression> by
    rightAssociative(orChain, impLiteral) { l, _, r ->
        Imp(l, r)
    }

    override val rootParser by impChain
}
