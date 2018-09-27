package ru.hse.spb

import org.junit.Assert.assertEquals
import org.junit.Test

public class BundleTest : AbstractTest() {


    @Test
    fun testProgram1() {
        val tree = getAst("var a = 10 var b = 20 if (a > b) {println(1)} else {println(0)}")
        MyVisitor().visit(tree)
        assertEquals("0", outContent.toString().trimEnd())
    }

    @Test
    fun testProgram2() {
        val tree = getAst(
                """fun fib(n) {
                    if (n <= 1) {
                       return 1
                    }
                    return fib(n - 1) + fib(n - 2)
                    }
                    var i = 1
                    while (i <= 5) {
                        println (i, fib(i))
                        i = i + 1
                    }""".trimMargin())
        MyVisitor().visit(tree)
        assertEquals("1 1\n2 2\n3 3\n4 4\n5 5", outContent.toString().trimEnd())
    }


    @Test
    fun testProgram3() {
        val tree = getAst("""
            fun foo(n) {
                fun bar(m) {
                    return m + n
                }
                return bar(1)
            }
            println(foo(41)) //prints 42""".trimMargin())
        MyVisitor().visit(tree)
        assertEquals("42", outContent.toString().trimEnd())
    }

    @Test
    fun testNesting() {
        val tree = getAst("""
            fun f1() {

                fun f2() {
                    println(b)
                }

                println(a)

                var b = 6
                f2()
            }

            var a = 5
            f1()""".trimMargin())
        MyVisitor().visit(tree)
        assertEquals("5\n6", outContent.toString().trimEnd())
    }

    @Test(expected = InterpreterException :: class)
    fun testNestingError() {
        val tree = getAst("""
            fun f1() {
                println(a)
                var b = 6
                f2()
            }

            fun f2() {
                    println(b)
            }

            var a = 5
            f1()""".trimMargin())
        MyVisitor().visit(tree)
    }

    @Test(expected = InterpreterException :: class)
    fun testNotDeclaring() {
        val tree = getAst("""
            a = 5
            """.trimMargin())
        MyVisitor().visit(tree)
    }

}