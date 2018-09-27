package ru.hse.spb

import ru.hse.spb.parser.ExpLexer
import ru.hse.spb.parser.ExpParser
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream


fun main(args: Array<String>) {
    val text = CharStreams.fromFileName(args.first())
    val expLexer = ExpLexer(text)
    val parser = ExpParser(CommonTokenStream(expLexer))
    parser.buildParseTree = true
    val tree = parser.file()
    val visitor = MyVisitor()


    try {
        visitor.visit(tree)
    } catch (exception: IllegalStateException) {
        System.err.println("Parsing exception: " + exception.message)
    } catch (exception: InterpreterException) {
        System.err.println("Interpretation exception: " + exception.message)
    }
}