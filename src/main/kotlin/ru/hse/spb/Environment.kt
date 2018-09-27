package ru.hse.spb

import ru.hse.spb.parser.ExpParser

class Environment(var parent: Environment? = null) {

    var vars: MutableMap<String, Int> = LinkedHashMap()
    var funcs: MutableMap<String, Pair<ExpParser.FunctionContext, Environment>> = LinkedHashMap()


    fun initVariable(name: String, value: Int? = 0) {
        vars[name] = value ?: 0
    }


    fun setVariable(name: String, value: Int) {
        return if (name in vars)
            vars[name] = value
        else
            parent?.setVariable(name, value) ?: throw InterpreterException("$name is not assigned")
    }


    fun getVariable(name: String): Int? {
        return if (name in vars)
            vars.getValue(name)
        else
            parent?.getVariable(name) ?: throw InterpreterException("$name is not assigned")
    }


    fun setFunction(name: String, value: ExpParser.FunctionContext, e : Environment) {
        if (name in funcs)
            throw InterpreterException("$name is already created")
        funcs[name] = Pair(value, e)
    }


    fun getFunction(name: String): Pair<ExpParser.FunctionContext, Environment> {
        return if (name in funcs)
            funcs.getValue(name)
        else
            parent?.getFunction(name) ?: throw InterpreterException("$name is not assigned")
    }

    fun copy() : Environment {
        val newEnv = Environment()
        newEnv.funcs = funcs
        newEnv.vars = vars
        newEnv.parent = parent?.copy()
        return newEnv
    }
}