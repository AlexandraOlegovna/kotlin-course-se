package ru.hse.spb

import ru.hse.spb.parser.ExpBaseVisitor
import ru.hse.spb.parser.ExpParser

class MyVisitor : ExpBaseVisitor<Int?>() {

    var env: Environment = Environment()


    override fun visitBlock(ctx: ExpParser.BlockContext): Int? {
        val newEnv = Environment(env)
        val oldEnv = env
        env = newEnv

        for (stmt in ctx.statement()) {
            if (stmt.returnStatement() != null || stmt.whileStatement() != null || stmt.ifStatement() != null) {
                val value = visit(stmt)
                val containsReturnStmt = value != null
                if (containsReturnStmt) {
                    env = oldEnv
                    return value
                }
            } else {
                visit(stmt)
            }
        }

        env = oldEnv
        return null
    }


    override fun visitBinaryE(ctx: ExpParser.BinaryEContext): Int? {
        fun toInt(x: Boolean) = if (x) 1 else 0

        fun toBoolean(x: Int) = x != 0

        val leftArg = visit(ctx.atomicE())
        val rightArg = visit(ctx.expression())

        if (leftArg == null || rightArg == null)
            return null

        return when (ctx.op.text) {
            "*" -> leftArg * rightArg
            "/" -> if (rightArg != 0) leftArg / rightArg else throw InterpreterException("division by zero")
            "%" -> if (rightArg != 0) leftArg % rightArg else throw InterpreterException("division by zero")
            "+" -> leftArg + rightArg
            "-" -> leftArg - rightArg
            ">" -> toInt(leftArg > rightArg)
            "<" -> toInt(leftArg < rightArg)
            ">=" -> toInt(leftArg >= rightArg)
            "<=" -> toInt(leftArg <= rightArg)
            "==" -> toInt(leftArg == rightArg)
            "!=" -> toInt(leftArg != rightArg)
            "&&" -> toInt(toBoolean(leftArg) && toBoolean(rightArg))
            "||" -> toInt(toBoolean(leftArg) || toBoolean(rightArg))
            else -> null
        }
    }


    override fun visitBlockWithBraces(ctx: ExpParser.BlockWithBracesContext): Int? {
        return visit(ctx.block())
    }


    override fun visitFunction(ctx: ExpParser.FunctionContext): Int? {
        val funcName = ctx.Identifier().text
        val newEnv = env.copy()
        env.setFunction(funcName, ctx, newEnv)
        return null
    }


    override fun visitVariable(ctx: ExpParser.VariableContext): Int? {
        val varName = ctx.Identifier().text
        env.initVariable(varName)
        if (ctx.expression() != null)
            env.setVariable(varName, visit(ctx.expression()) ?: 0)
        return null
    }


    override fun visitWhileStatement(ctx: ExpParser.WhileStatementContext): Int? {
        while (true) {
            val isConditionTrue = (visit(ctx.expression()) ?: 0) != 0
            if (!isConditionTrue)
                return null
            val result = visit(ctx.blockWithBraces())
            val containsReturnStmt = result != null
            if (containsReturnStmt)
                return result
        }
    }


    override fun visitIfStatement(ctx: ExpParser.IfStatementContext): Int? {
        val isConditionTrue = (visit(ctx.expression()) ?: 0) != 0
        val branches = ctx.blockWithBraces()
        val thereIsElseBranch = branches.size == 2
        return when {
            isConditionTrue -> visit(ctx.blockWithBraces(0))
            thereIsElseBranch -> visit(ctx.blockWithBraces(1))
            else -> null
        }
    }


    override fun visitAssignment(ctx: ExpParser.AssignmentContext): Int? {
        val varName = ctx.Identifier().text
        val value = visit(ctx.expression())
        env.setVariable(varName, value ?: 0)
        return null
    }


    override fun visitReturnStatement(ctx: ExpParser.ReturnStatementContext): Int? {
        return visit(ctx.expression())
    }


    private fun printlnExpression(args: List<Int?>): Int? {
        println(args.filter { it != null }.joinToString(" "))
        return null
    }


    override fun visitFunctionCall(ctx: ExpParser.FunctionCallContext): Int? {
        val funcName = ctx.Identifier().text
        val args = ctx.arguments().expression().map { visit(it) }
        if (funcName == "println")
            return this.printlnExpression(args)
        val functionAndEnv = env.getFunction(funcName)
        val function = functionAndEnv.first
        val functionEnv = functionAndEnv.second
        val parameters = function.parameterNames().Identifier().map { it.text }
        if (parameters.size != args.size)
            throw InterpreterException("Invalid number of arguments")

        val oldEnv = env
        env = functionEnv

        parameters.zip(args, env::initVariable)
        val result = visit(function.blockWithBraces()) ?: 0

        env = oldEnv
        return result
    }


    override fun visitIndentifierAtomicExpr(ctx: ExpParser.IndentifierAtomicExprContext): Int? {
        val varName = ctx.Identifier().text
        return env.getVariable(varName)
    }


    override fun visitLiteralAtomicExpr(ctx: ExpParser.LiteralAtomicExprContext): Int? {
        return ctx.text.toInt()
    }


    override fun visitExpressionsAtomicExpr(ctx: ExpParser.ExpressionsAtomicExprContext): Int? {
        return visit(ctx.expression())
    }

}

public class InterpreterException(override var message: String) : Exception(message)