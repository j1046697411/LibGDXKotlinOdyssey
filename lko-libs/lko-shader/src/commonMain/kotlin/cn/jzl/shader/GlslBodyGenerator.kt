package cn.jzl.shader

import cn.jzl.shader.operand.*
import cn.jzl.shader.struct.StructDefinition

class GlslBodyGenerator : Visitor<Indenter>() {

    fun getVarName(type: VarType): String {
        return when (type) {
            is VarType.Float -> "float"
            is VarType.FloatVec2 -> "vec2"
            is VarType.FloatVec3 -> "vec3"
            is VarType.FloatVec4 -> "vec4"

            is VarType.Integer -> "int"
            is VarType.IntVec2 -> "ivec2"
            is VarType.IntVec3 -> "ivec3"
            is VarType.IntVec4 -> "ivec4"

            is VarType.Bool -> "bool"
            is VarType.BoolVec2 -> "bvec2"
            is VarType.BoolVec3 -> "bvec3"
            is VarType.BoolVec4 -> "bvec4"

            is VarType.Struct<*> -> type.structName
            is VarType.Void -> "void"

            is VarType.Sampler1D -> "sampler1D"
            is VarType.Sampler2D -> "sampler2D"
            is VarType.Sampler3D -> "sampler3D"
            is VarType.SamplerCube -> "samplerCube"

            is VarType.Mat2 -> "mat2"
            is VarType.Mat3 -> "mat3"
            is VarType.Mat4 -> "mat4"
            else -> ""
        }
    }

    override fun visit(statement: FunctionDefinition<*>, out: Indenter): Indenter {
        return out.inline("${getVarName(statement.returnType)} ${statement.name}(", ") ") {
            statement.args.foldIndexed(this) { index, acc, arg -> if (index == 0) acc.inline("${getVarName(arg.type)} ${arg.name}") else acc.inline(", ${getVarName(arg.type)} ${arg.name}") }
        }.inline {
            visit(statement.body, this)
        }
    }

    override fun visit(structDefinition: StructDefinition<*>, out: Indenter): Indenter {
        val struct = structDefinition.constructor(structDefinition.shaderScope, "visit")
        return out.inline("struct ${struct.structName} ").block(suffix = ";") {
            struct.fold(this) { acc, property -> acc.line("${getVarName(property.type)} ${property.name};") }
        }
    }

    override fun visit(literal: Literal.FloatLiteral, out: Indenter): Indenter = out.inline(literal.value.toString())

    override fun visit(literal: Literal.IntLiteral, out: Indenter): Indenter = out.inline(String.format("%d", literal.value))

    override fun visit(literal: Literal.BooleanLiteral, out: Indenter): Indenter = out.inline(literal.value.toString())

    override fun visit(func: Func<*>, out: Indenter): Indenter {
        return out.inline("${func.name}(", ")") {
            func.params.foldIndexed(this) { index, acc, operand -> visit(operand, if (index == 0) acc else acc.inline(", ")) }
        }
    }

    override fun visit(variable: Variable<*>, out: Indenter): Indenter {
        return out.inline(variable.name)
    }

    override fun visit(swizzle: Swizzle<*>, out: Indenter): Indenter {
        return visit(swizzle.left, out).inline(".").inline(swizzle.swizzle)
    }

    override fun visit(operator: Operator.BinaryOperator<*>, out: Indenter): Indenter {
        return visit(operator.right, visit(operator.left, out).inline(" ${operator.symbol} "))
    }

    override fun visit(operator: Operator.UnaryOperator<*>, out: Indenter): Indenter {
        return visit(operator.right, out.inline(operator.symbol))
    }

    override fun visit(statement: VariableDefinition<*>, out: Indenter): Indenter {
        return out.inline("${getVarName(statement.variable.type)} ${statement.variable.name}", ";") {
            statement.value?.let { visit(it, this.inline(" = ")) } ?: this
        }.emptyLine()
    }

    override fun visit(operator: Operator.TernaryOperator<*>, out: Indenter): Indenter {
        return visit(operator.left, visit(operator.condition, visit(operator.right, out)))
    }

    override fun visit(statement: Statement.CodeBlock, out: Indenter): Indenter {
        return out.block { statement.statements.fold(this) { acc, statement -> visit(statement, acc) } }
    }

    override fun visit(statement: Statement.Assignment, out: Indenter): Indenter {
        return visit(statement.value, visit(statement.variable, out).inline(" = ")).line(";")
    }

    override fun visit(statement: Statement.If, out: Indenter): Indenter {
        return out.inline("if (", ") ") {
            visit(statement.condition, this)
        }.inline { visit(statement.body, this) }
    }

    override fun visit(statement: Statement.ElseIf, out: Indenter): Indenter {
        return out.inline("else if (", ") ") {
            visit(statement.condition, this)
        }.inline { visit(statement.body, this) }
    }

    override fun visit(statement: Statement.Else, out: Indenter): Indenter {
        return out.inline("else ") { visit(statement.body, this) }
    }

    override fun visit(statement: Statement.While, out: Indenter): Indenter {
        return out.inline("while (", ")") {
            visit(statement.condition, this)
        }.block {
            visit(statement.body, this)
        }
    }

    override fun visit(statement: Statement.For, out: Indenter): Indenter {
        return out.inline("for (", ")") {
            val loopVar = statement.loopVar()
            visit(statement.condition(loopVar), visit(loopVar, this).inline(";"))
                .let { visit(statement.update(loopVar), this) }
        }.block {
            visit(statement.body, this)
        }
    }

    override fun visit(statement: Statement.Break, out: Indenter): Indenter {
        return out.line("break;")
    }

    override fun visit(statement: Statement.Continue, out: Indenter): Indenter {
        return out.line("continue;")
    }

    override fun visit(statement: Statement.Return<*>, out: Indenter): Indenter {
        return out.inline("return") { visit(statement.value, if (statement.value != Operand.Void) this.inline(" ") else this) }.line(";")
    }

    override fun visit(statement: Statement.Discard, out: Indenter): Indenter {
        return out.line("discard;")
    }
}