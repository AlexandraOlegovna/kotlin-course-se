package ru.hse.spb

import org.junit.Test
import org.junit.Assert.assertEquals

public class InterpretingTest : AbstractTest() {

    @Test
    fun testVariable() {
        val tree1 = getAst("var x = 42").block().statement()[0]
        val visitor1 = MyVisitor()
        visitor1.visit(tree1)
        assertEquals(visitor1.env.vars["x"], 42)

        val tree2 = getAst("var x").block().statement()[0]
        val visitor2 = MyVisitor()
        visitor2.visit(tree2)
        assertEquals(visitor2.env.vars["x"], 0)

        val tree3 = getAst("x = 42").block().statement()[0]
        val visitor3 = MyVisitor()
        visitor3.env.vars["x"] = 0
        visitor3.visit(tree3)
        assertEquals(visitor3.env.vars["x"], 42)
    }


    @Test
    fun testExpression() {
        val tree1 = getAst("var x = 37 + s").block().statement()[0]
        val visitor1 = MyVisitor()
        visitor1.env.vars["s"] = 5
        visitor1.visit(tree1)
        assertEquals(visitor1.env.vars["x"], 42)

        val tree2 = getAst("var x = 1 + 2 * s").block().statement()[0]
        val visitor2 = MyVisitor()
        visitor2.env.vars["s"] = 5
        visitor2.visit(tree2)
        assertEquals(visitor2.env.vars["x"], 11)

        val tree3 = getAst("var x = (a <= b) * c && d").block().statement()[0]
        val visitor3 = MyVisitor()
        visitor3.env.vars["a"] = 2
        visitor3.env.vars["b"] = 1
        visitor3.env.vars["c"] = 100
        visitor3.env.vars["d"] = 42
        visitor3.visit(tree3)
        assertEquals(visitor3.env.vars["x"], 0)

    }


    @Test
    fun testAddFunction() {
        val tree1 = getAst("var x = f1(5)").block().statement()[0]
        val f1 = getAst("fun f1(x) {return x + 37}")
        val visitor1 = MyVisitor()

        visitor1.env.funcs["f1"] = Pair(f1.block().statement()[0].function(), Environment())
        visitor1.visit(tree1)
        assertEquals(visitor1.env.vars["x"], 42)
    }


    @Test
    fun testFunctionAndExpression() {
        val tree1 = getAst("var x = f1(5)").block().statement()[0]
        val f1 = getAst("fun f1(x) {return x + 37}")
        val visitor1 = MyVisitor()

        visitor1.env.funcs["f1"] = Pair(f1.block().statement()[0].function(), Environment())
        visitor1.visit(tree1)
        assertEquals(visitor1.env.vars["x"], 42)
    }


    @Test
    fun testReturn() {
        val tree = getAst("return s + 1").block().statement()[0]
        val visitor = MyVisitor()
        visitor.env.vars["s"] = 5
        val result = visitor.visit(tree)
        assertEquals(result, 6)
    }


    @Test
    fun testWhile() {
        val tree = getAst("while (s < 3) {return s + 1}")
        val visitor = MyVisitor()
        visitor.env.vars["s"] = 1
        val result = visitor.visit(tree)
        assertEquals(result, 2)
    }


    @Test
    fun testIf() {
        val tree = getAst("if (s < 3) {return s + 1} else {return s}")
        val visitor = MyVisitor()
        visitor.env.vars["s"] = 1
        val result = visitor.visit(tree)
        assertEquals(result, 2)
    }

}