package ru.hse.spb

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.junit.After
import org.junit.Before
import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import java.io.ByteArrayOutputStream
import java.io.PrintStream

abstract class AbstractTest {

    protected val outContent = ByteArrayOutputStream()
    protected val originalOut = System.out

    @Before
    fun setUpStreams() {
        System.setOut(PrintStream(outContent))
    }

    @After
    fun restoreStreams() {
        System.setOut(originalOut)
    }

    protected fun getAst(text : String) : ExpParser.FileContext {
        val stream = CharStreams.fromString(text)
        val expLexer = ExpLexer(stream)
        val parser = ExpParser(CommonTokenStream(expLexer))
        parser.buildParseTree = true
        return parser.file()
    }

}