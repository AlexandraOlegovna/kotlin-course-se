package ru.hse.spb

import org.junit.Test
import ru.hse.spb.parser.ExpParser
import org.junit.Assert.assertEquals


public class ParsingTest : AbstractTest() {


    @Test
    fun testVariable() {
        val tree1 = getAst("var x")
        val variable1 = tree1.block().statement()[0].variable()
        assertEquals(variable1.Identifier().text, "x")
        assertEquals(variable1.expression(), null)

        val tree2 = getAst("x = 1")
        val variable2 = tree2.block().statement()[0].assignment()
        assertEquals(variable2.Identifier().text, "x")
        assertEquals(variable2.expression().atomicE().text, "1")

        val tree3 = getAst("var x = 1")
        val variable3 = tree3.block().statement()[0].variable()
        assertEquals(variable3.Identifier().text, "x")
        assertEquals(variable3.expression().atomicE().text, "1")

    }


    @Test
    fun testFunction() {
        val func1 = getAst("fun f1() {}").block().statement()[0].function()
        assertEquals(func1.Identifier().text, "f1")
        assertEquals(func1.parameterNames().Identifier().size, 0)
        assertEquals(func1.blockWithBraces().block().statement().size, 0)


        val tree = getAst("fun f2(x){} f2(5)").block()
        val func2 = tree.statement()[0].function()
        assertEquals(func2.Identifier().text, "f2")
        assertEquals(func2.parameterNames().Identifier().size, 1)
        assertEquals(func2.parameterNames().Identifier()[0].text, "x")
        assertEquals(func2.blockWithBraces().block().statement().size, 0)

        val main = tree.statement()[1].expression().atomicE() as ExpParser.FunctionCallAtomicExprContext
        assertEquals(main.functionCall().Identifier().text, "f2")
        assertEquals(main.functionCall().arguments().expression()[0].atomicE().text, "5")

    }


    @Test
    fun testProgram1() {
        val tree = getAst("var a = 10 var b = 20 if (a > b) {println(1)} else {println(0)}")

        val variable1 = tree.block().statement()[0].variable()
        val variable2 = tree.block().statement()[1].variable()
        assertEquals(variable1.Identifier().text, "a")
        assertEquals(variable1.expression().atomicE().text, "10")
        assertEquals(variable2.Identifier().text, "b")
        assertEquals(variable2.expression().atomicE().text, "20")

        val ifStatement = tree.block().statement()[2].ifStatement()
        assertEquals(ifStatement.expression().binaryE().atomicE().text, "a")
        assertEquals(ifStatement.expression().binaryE().op.text, ">")
        assertEquals(ifStatement.expression().binaryE().expression().text, "b")

        val thenBlock = ifStatement.blockWithBraces(0).block().statement()[0].expression().atomicE() as ExpParser.FunctionCallAtomicExprContext
        assertEquals(thenBlock.functionCall().Identifier().text, "println")
        assertEquals(thenBlock.functionCall().arguments().expression()[0].text, "1")

        val elseBlock = ifStatement.blockWithBraces(1).block().statement()[0].expression().atomicE() as ExpParser.FunctionCallAtomicExprContext
        assertEquals(elseBlock.functionCall().Identifier().text, "println")
        assertEquals(elseBlock.functionCall().arguments().expression()[0].text, "0")
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

        val fibFunction = tree.block().statement()[0].function()
        val fibFunctionCode = fibFunction.blockWithBraces().block().statement()
        assertEquals(fibFunction.Identifier().text, "fib")
        assertEquals(fibFunctionCode[0].ifStatement().expression().text, "n<=1")
        val fibFunctionReturn = fibFunctionCode[1].returnStatement().expression()
        assertEquals(fibFunctionReturn.binaryE().op.text, "+")
        assertEquals(fibFunctionReturn.binaryE().atomicE().text, "fib(n-1)")
        assertEquals(fibFunctionReturn.binaryE().expression().atomicE().text, "fib(n-2)")

        val whileStmt = tree.block().statement()[2].whileStatement()
        val whileStmtCode = whileStmt.blockWithBraces().block().statement()
        val printlnFunction = whileStmtCode[0].expression().atomicE() as ExpParser.FunctionCallAtomicExprContext
        assertEquals(whileStmt.expression().binaryE().text, "i<=5")
        assertEquals(printlnFunction.functionCall().Identifier().text, "println")
        assertEquals(printlnFunction.functionCall().arguments().expression()[0].atomicE().text, "i")
        assertEquals(printlnFunction.functionCall().arguments().expression()[1].atomicE().text, "fib(i)")
        assertEquals(whileStmtCode[1].assignment().Identifier().text, "i")
        assertEquals(whileStmtCode[1].assignment().expression().binaryE().op.text, "+")
        assertEquals(whileStmtCode[1].assignment().expression().binaryE().atomicE().text, "i")
        assertEquals(whileStmtCode[1].assignment().expression().binaryE().expression().atomicE().text, "1")
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

        val fooFunction = tree.block().statement()[0].function()
        val returnFoo = fooFunction.blockWithBraces().block().statement()[1].returnStatement().expression().atomicE() as ExpParser.FunctionCallAtomicExprContext
        val barFunction = fooFunction.blockWithBraces().block().statement()[0].function()
        val returnBar = barFunction.blockWithBraces().block().statement()[0].returnStatement()

        assertEquals(fooFunction.Identifier().text, "foo")
        assertEquals(fooFunction.parameterNames().Identifier()[0].text, "n")
        assertEquals(barFunction.Identifier().text, "bar")
        assertEquals(barFunction.parameterNames().Identifier()[0].text, "m")
        assertEquals(returnBar.expression().binaryE().op.text, "+")
        assertEquals(returnBar.expression().binaryE().atomicE().text, "m")
        assertEquals(returnBar.expression().binaryE().expression().atomicE().text, "n")
        assertEquals(returnFoo.functionCall().Identifier().text, "bar")
        assertEquals(returnFoo.functionCall().arguments().expression(0).atomicE().text, "1")
    }

}