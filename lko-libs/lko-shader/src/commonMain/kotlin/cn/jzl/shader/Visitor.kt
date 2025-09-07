package cn.jzl.shader

import cn.jzl.shader.operand.Func
import cn.jzl.shader.operand.Literal
import cn.jzl.shader.operand.Operand
import cn.jzl.shader.operand.Operator
import cn.jzl.shader.operand.Swizzle
import cn.jzl.shader.operand.SystemFunc
import cn.jzl.shader.operand.Variable
import cn.jzl.shader.struct.StructDefinition

open class Visitor<T> {

    open fun visit(program: Program, out: T): T {
        return visit(program.fragmentShader, visit(program.vertexShader, out))
    }

    open fun visit(vertexShader: Program.VertexShader, out: T): T {
        val structs = vertexShader.structs.fold(out) { acc, definition -> visit(definition, acc) }
        return vertexShader.functions.fold(structs) { acc, definition -> visit(definition, acc) }
    }

    open fun visit(fragmentShader: Program.FragmentShader, out: T): T {
        val structs = fragmentShader.structs.fold(out) { acc, definition -> visit(definition, acc) }
        return fragmentShader.functions.fold(structs) { acc, definition -> visit(definition, acc) }
    }

    open fun visit(structDefinition: StructDefinition<*>, out: T): T = out

    open fun visit(operand: Operand<*>, out: T): T {
        return when (operand) {
            is Variable -> visit(operand, out)
            is Literal<*, *> -> visit(operand, out)
            is Swizzle -> visit(operand, out)
            is Operator<*> -> visit(operand, out)
            is Func<*> -> visit(operand, out)
            else -> out
        }
    }

    open fun visit(func: Func<*>, out: T): T {
        return when (func) {
            is SystemFunc<*> -> visit(func, out)
            is CustomFunc<*> -> visit(func, out)
            else -> out
        }
    }

    open fun visit(systemFunc: SystemFunc<*>, out: T): T = out
    open fun visit(customFunc: CustomFunc<*>, out: T): T = out

    open fun visit(variable: Variable<*>, out: T): T = out

    open fun visit(literal: Literal<*, *>, out: T): T {
        return when (literal) {
            is Literal.IntLiteral -> visit(literal, out)
            is Literal.FloatLiteral -> visit(literal, out)
            is Literal.BooleanLiteral -> visit(literal, out)
            else -> out
        }
    }

    open fun visit(literal: Literal.IntLiteral, out: T): T = out
    open fun visit(literal: Literal.FloatLiteral, out: T): T = out
    open fun visit(literal: Literal.BooleanLiteral, out: T): T = out

    open fun visit(swizzle: Swizzle<*>, out: T): T = out

    open fun visit(operator: Operator<*>, out: T): T {
        return when (operator) {
            is Operator.BinaryOperator<*> -> visit(operator, out)
            is Operator.UnaryOperator<*> -> visit(operator, out)
            is Operator.TernaryOperator<*> -> visit(operator, out)
            else -> out
        }
    }

    open fun visit(operator: Operator.BinaryOperator<*>, out: T): T {
        return visit(operator.right, visit(operator.left, out))
    }

    open fun visit(operator: Operator.UnaryOperator<*>, out: T): T {
        return visit(operator.right, out)
    }

    open fun visit(operator: Operator.TernaryOperator<*>, out: T): T {
        return visit(operator.left, visit(operator.condition, visit(operator.right, out)))
    }

    open fun visit(statement: Statement, out: T): T {
        return when (statement) {
            is VariableDefinition<*> -> visit(statement, out)
            is Statement.CodeBlock -> visit(statement, out)
            is Statement.Assignment -> visit(statement, out)
            is Statement.If -> visit(statement, out)
            is Statement.ElseIf -> visit(statement, out)
            is Statement.Else -> visit(statement, out)
            is Statement.While -> visit(statement, out)
            is Statement.For -> visit(statement, out)
            is Statement.Break -> visit(statement, out)
            is Statement.Continue -> visit(statement, out)
            is Statement.Return<*> -> visit(statement, out)
            is Statement.Discard -> visit(statement, out)
            is FunctionDefinition<*> -> visit(statement, out)
            else -> out
        }
    }

    open fun visit(statement: FunctionDefinition<*>, out: T): T = out

    open fun visit(statement: VariableDefinition<*>, out: T): T {
        return visit(statement.variable, out).let { tem -> statement.value?.let { visit(it, tem) } ?: tem }
    }

    open fun visit(statement: Statement.CodeBlock, out: T): T = statement.statements.fold(out) { acc, statement -> visit(statement, acc) }

    open fun visit(statement: Statement.Assignment, out: T): T = visit(statement.value, visit(statement.variable, out))

    open fun visit(statement: Statement.If, out: T): T = visit(statement.body, visit(statement.condition, out))

    open fun visit(statement: Statement.ElseIf, out: T): T = visit(statement.body, visit(statement.condition, out))

    open fun visit(statement: Statement.Else, out: T): T = visit(statement.body, out)

    open fun visit(statement: Statement.While, out: T): T = visit(statement.body, visit(statement.condition, out))

    open fun visit(statement: Statement.For, out: T): T {
        val loopVar = statement.loopVar()
        return visit(statement.body, visit(statement.update(loopVar), visit(statement.condition(loopVar), visit(loopVar, out))))
    }

    open fun visit(statement: Statement.Break, out: T): T = out

    open fun visit(statement: Statement.Continue, out: T): T = out

    open fun visit(statement: Statement.Return<*>, out: T): T = visit(statement.value, out)

    open fun visit(statement: Statement.Discard, out: T): T = out
}