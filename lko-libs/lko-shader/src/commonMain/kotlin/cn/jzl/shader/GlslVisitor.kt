package cn.jzl.shader

import cn.jzl.shader.Operand
import cn.jzl.shader.Statement
import cn.jzl.shader.Struct
import cn.jzl.shader.StructDeclaration
import cn.jzl.shader.VarType
import cn.jzl.shader.Visitor

class GlslVisitor : Visitor<Indenter>() {

    override fun visit(varType: VarType, out: Indenter): Indenter {
        val typeName = when (varType) {
            is VarType.Void -> "void"
            is VarType.Integer -> "int"
            is VarType.Float -> "float"
            is Struct<*> -> varType.structName
            else -> throw IllegalArgumentException("Unknown var type: $varType")
        }
        return out.inline(typeName)
    }

    override fun visit(intLiteral: Operand.Literal.IntLiteral, out: Indenter): Indenter {
        return out.inline(intLiteral.value.toString())
    }

    override fun visit(booleanLiteral: Operand.Literal.BooleanLiteral, out: Indenter): Indenter {
        return out.inline(booleanLiteral.value.toString())
    }

    override fun visit(floatLiteral: Operand.Literal.FloatLiteral, out: Indenter): Indenter {
        return out.inline(floatLiteral.value.toString())
    }

    override fun visit(variable: Operand.Variable<*>, out: Indenter): Indenter {
        return out.inline(variable.name)
    }

    override fun visit(function: Operand.Function<*>, out: Indenter): Indenter {
        return out.inline(function.name).inline("(", ") ") {
            function.args.fold(this) { acc, operand -> visit(operand, acc) }
        }
    }

    override fun visit(binaryOperator: Operand.Operator.BinaryOperator<*>, out: Indenter): Indenter {
        return visit(binaryOperator.left, out)
            .inline(" ${binaryOperator.symbol} ")
            .let { visit(binaryOperator.right, it) }
    }

    override fun visit(returnValue: Statement.Return<*>, out: Indenter): Indenter {
        return out.inline("return ")
            .let { visit(returnValue.value, out) }
            .line(";")
    }

    override fun visit(swizzle: Operand.Swizzle<*>, out: Indenter): Indenter {
        return visit(swizzle.left, out).inline(".").inline(swizzle.swizzle)
    }

    override fun visit(definition: Statement.Definition<*, *>, out: Indenter): Indenter {
        return visit(definition.variable.type, out)
            .inline(" ")
            .let { visit(definition.variable, it) }
            .let { definition.value?.let { value -> visit(value, it.inline(" = ")) } ?: out }
            .let { if (definition !is Statement.ArgDefinition) it.line(";") else it }
    }

    override fun visit(assignment: Statement.Assignment<*>, out: Indenter): Indenter {
        return visit(assignment.value, visit(assignment.variable, out).inline(" = "))
    }

    override fun visit(codeBlock: Statement.CodeBlock, out: Indenter): Indenter {
        return out.block { codeBlock.statements.fold(this) { acc, statement -> visit(statement, acc) } }
    }

    override fun visit(ifStatement: Statement.If, out: Indenter): Indenter {
        return out.inline("if ").inline("(", ")") {
            visit(ifStatement.condition, this)
        }.let { visit(ifStatement.body, it) }
    }

    override fun visit(elseIfStatement: Statement.ElseIf, out: Indenter): Indenter {
        return out.inline(" else if (", ") ") {
            visit(elseIfStatement.condition, this)
        }.let { visit(elseIfStatement.body, it) }
    }

    override fun visit(elseStatement: Statement.Else, out: Indenter): Indenter {
        return out.inline("else ") { visit(elseStatement.body, this) }
    }

    override fun visit(forStatement: Statement.For, out: Indenter): Indenter {
        return out.inline("for").inline("(", ")") {
            visit(forStatement.init, this)
                .inline("; ")
                .let { visit(forStatement.condition, it) }
                .inline("; ")
                .let { visit(forStatement.update, it) }
        }.inline(" ") { visit(forStatement.body, this) }
    }

    override fun visit(structDeclaration: StructDeclaration<*>, out: Indenter): Indenter {
        return out.block("struct ${structDeclaration.struct.structName} ") {
            structDeclaration.struct.fold(this) { acc, arg -> visit(arg, acc) }
        }
    }

    override fun visit(functionDeclaration: FunctionDeclaration<*>, out: Indenter): Indenter {
        return visit(functionDeclaration.returnType, out)
            .inline(" ")
            .inline(functionDeclaration.name)
            .inline("(", ") ") {
                functionDeclaration.args.foldIndexed(this) { index, acc, arg ->
                    visit(arg, if (index == 0) acc else acc.inline(", "))

                }
            }.let { visit(functionDeclaration.body, it) }
    }
}