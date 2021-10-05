package task.c.solve

import task.c.types.Eq
import task.c.types.Expression
import task.c.types.ForAny
import task.c.types.Imp
import task.c.types.Var
import task.c.types.Zero

import org.junit.Test

import org.junit.Assert.*
import task.c.types.Add
import task.c.types.And
import task.c.types.Exists
import task.c.types.Inc
import task.c.types.Not
import task.c.types.Or
import task.c.types.Predicate

class ParseTest {

    @Test
    fun test1() {
        val testString = "0=0"
        val expected = Eq(Zero, Zero)
        val actual = Expression.parseExpression(testString)
        assertEquals(expected, actual)
    }

    @Test
    fun test2() {
        val testString = "((@p.(@x.(x=x)))->(@x.(x=x)))"
        val expected = Imp(
            ForAny(
                Var("p"),
                ForAny(
                    Var("x"),
                    Eq(
                        Var("x"),
                        Var("x"),
                    )
                ),
            ),
            ForAny(
                Var("x"),
                Eq(
                    Var("x"),
                    Var("x"),
                ),
            ),
        )
        val actual = Expression.parseExpression(testString)
        assertEquals(expected, actual)
    }

    @Test
    fun test3() {
        val testString = "A->(0''''+a=(((b))))->B"
        val expected = Imp(
            Predicate("A"),
            Imp(
                Eq(
                    Add(
                        Inc(Inc(Inc(Inc(Zero)))),
                        Var("a"),
                    ),
                    Var("b"),
                ),
                Predicate("B"),
            )
        )
        val actual = Expression.parseExpression(testString)
        assertEquals(expected, actual)
    }

    @Test
    fun test4() {
        val testString = "" +
            "(" +
                "(" +
                    "(" +
                        "(?x.(?y.((x+y)=p)))" +
                    "->" +
                        "(@x.(@y.((y+x)=r)))" +
                    ")" +
                "&" +
                    "((y+x)=r)" +
                ")" +
            "->" +
                    "(!(?y.((z+y)=p))" +
                "|" +
                    "((y+x)=r))" +
            ")" +
            ""
        val x = Var("x")
        val y = Var("y")
        val z = Var("z")
        val p = Var("p")
        val r = Var("r")
        val expected = Imp(
            And(
                Imp(
                    Exists(x, Exists(y, Eq(Add(x, y), p))),
                    ForAny(x, ForAny(y, Eq(Add(y, x), r))),
                ),
                Eq(Add(y, x), r),
            ),
            Or(
                Not(
                    Exists(y, Eq(Add(z, y), p)),
                ),
                Eq(Add(y, x), r),
            ),
        )
        val actual = Expression.parseExpression(testString)
        assertEquals(expected, actual)
    }
}
