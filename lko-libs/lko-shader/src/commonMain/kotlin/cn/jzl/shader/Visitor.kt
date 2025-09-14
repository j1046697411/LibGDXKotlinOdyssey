package cn.jzl.shader

open class Visitor<R> {

    open fun visit(varType: VarType, out: R): R = out

    open fun visit(program: Program, out: R): R {
        return visit(program.fragmentShader, visit(program.vertexShader, out))
    }

    open fun visit(vertexShader: Program.VertexShader, out: R): R {
        val structOut = vertexShader.structs.fold(out) { acc, declaration -> visit(declaration, acc) }
        return vertexShader.functions.fold(structOut) { acc, declaration -> visit(declaration, acc) }
    }

    open fun visit(fragmentShader: Program.FragmentShader, out: R): R {
        val structOut = fragmentShader.structs.fold(out) { acc, declaration -> visit(declaration, acc) }
        return fragmentShader.functions.fold(structOut) { acc, declaration -> visit(declaration, acc) }
    }

    open fun visit(structDeclaration: StructDeclaration<*>, out: R): R = out

    open fun visit(functionDeclaration: FunctionDeclaration<*>, out: R): R {
        return visit(functionDeclaration.body, functionDeclaration.args.fold(out) { acc, definition -> visit(definition, acc) })
    }

    open fun visit(statement: Statement, out: R): R {
        return when (statement) {
            is Statement.Return<*> -> visit(statement, out)
            is Statement.Assignment<*> -> visit(statement, out)
            is Statement.Definition<*, *> -> visit(statement, out)
            is Statement.CodeBlock -> visit(statement, out)
            is Statement.If -> visit(statement, out)
            is Statement.ElseIf -> visit(statement, out)
            is Statement.Else -> visit(statement, out)
            is Statement.For -> visit(statement, out)
            else -> out
        }
    }

    open fun visit(returnValue: Statement.Return<*>, out: R): R = visit(returnValue.value, out)
    open fun visit(assignment: Statement.Assignment<*>, out: R): R = visit(assignment.value, visit(assignment.variable, out))
    open fun visit(definition: Statement.Definition<*, *>, out: R): R = visit(definition.variable, definition.value?.let { visit(it, out) } ?: out)
    open fun visit(codeBlock: Statement.CodeBlock, out: R): R = codeBlock.statements.fold(out) { acc, statement -> visit(statement, acc) }
    open fun visit(ifStatement: Statement.If, out: R): R = visit(ifStatement.body, visit(ifStatement.condition, out))
    open fun visit(elseIfStatement: Statement.ElseIf, out: R): R = visit(elseIfStatement.body, visit(elseIfStatement.condition, out))
    open fun visit(elseStatement: Statement.Else, out: R): R = visit(elseStatement.body, out)
    open fun visit(forStatement: Statement.For, out: R): R = visit(forStatement.body, visit(forStatement.condition, visit(forStatement.init, visit(forStatement.update, out))))

    open fun visit(operand: Operand<*>, out: R): R {
        return when (operand) {
            is Operand.Variable -> visit(operand, out)
            is Operand.Swizzle -> visit(operand, out)
            is Operand.Void -> visit(operand, out)
            is Operand.Literal<*, *> -> visit(operand, out)
            is Operand.Operator<*> -> visit(operand, out)
            is Operand.Function<*> -> visit(operand, out)
            else -> out
        }
    }

    open fun visit(variable: Operand.Variable<*>, out: R): R = out
    open fun visit(void: Operand.Void, out: R): R = out
    open fun visit(literal: Operand.Literal<*, *>, out: R): R {
        return when (literal) {
            is Operand.Literal.FloatLiteral -> visit(literal, out)
            is Operand.Literal.IntLiteral -> visit(literal, out)
            is Operand.Literal.BooleanLiteral -> visit(literal, out)
        }
    }

    open fun visit(floatLiteral: Operand.Literal.FloatLiteral, out: R): R = out
    open fun visit(intLiteral: Operand.Literal.IntLiteral, out: R): R = out
    open fun visit(booleanLiteral: Operand.Literal.BooleanLiteral, out: R): R = out

    open fun visit(operator: Operand.Operator<*>, out: R): R {
        return when (operator) {
            is Operand.Operator.UnaryOperator<*> -> visit(operator, out)
            is Operand.Operator.TernaryOperator<*> -> visit(operator, out)
            is Operand.Operator.BinaryOperator<*> -> visit(operator, out)
        }
    }

    open fun visit(unaryOperator: Operand.Operator.UnaryOperator<*>, out: R): R = visit(unaryOperator.right, out)
    open fun visit(ternaryOperator: Operand.Operator.TernaryOperator<*>, out: R): R = visit(ternaryOperator.condition, visit(ternaryOperator.left, visit(ternaryOperator.right, out)))
    open fun visit(binaryOperator: Operand.Operator.BinaryOperator<*>, out: R): R = visit(binaryOperator.left, visit(binaryOperator.right, out))

    open fun visit(function: Operand.Function<*>, out: R): R = function.args.fold(out) { acc, argument -> visit(argument, acc) }
    open fun visit(swizzle: Operand.Swizzle<*>, out: R): R = visit(swizzle.left, out)
}