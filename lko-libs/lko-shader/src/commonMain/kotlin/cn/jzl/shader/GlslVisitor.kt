package cn.jzl.shader

class GlslVisitor : Visitor<Indenter>() {

    override fun visit(varType: VarType, out: Indenter): Indenter {
        val typeName = when (varType) {
            is VarType.Void -> "void"

            is VarType.Integer -> "int"
            is VarType.IVec2 -> "ivec2"
            is VarType.IVec3 -> "ivec3"
            is VarType.IVec4 -> "ivec4"

            is VarType.Float -> "float"
            is VarType.Vec2 -> "vec2"
            is VarType.Vec3 -> "vec3"
            is VarType.Vec4 -> "vec4"
            is Struct<*> -> varType.structName

            is VarType.Boolean -> "bool"
            is VarType.BVec2 -> "bvec2"
            is VarType.BVec3 -> "bvec3"
            is VarType.BVec4 -> "bvec4"

            is VarType.Mat2 -> "mat2"
            is VarType.Mat3 -> "mat3"
            is VarType.Mat4 -> "mat4"

            is VarType.Sampler1D -> "sampler1D"
            is VarType.Sampler2D -> "sampler2D"
            is VarType.Sampler3D -> "sampler3D"
            is VarType.SamplerCube -> "samplerCube"

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
        return out.inline(function.name).inline("(", ")") {
            function.args.foldIndexed(this) { index, acc, operand -> visit(operand, if (index == 0) acc else acc.inline(", ")) }
        }
    }

    override fun visit(binaryOperator: Operand.Operator.BinaryOperator<*>, out: Indenter): Indenter {
        return visit(binaryOperator.left, out)
            .inline(" ${binaryOperator.symbol} ")
            .let { visit(binaryOperator.right, it) }
    }

    override fun visit(returnValue: Statement.Return<*>, out: Indenter): Indenter {
        return out.inline("return")
            .let { visit(returnValue.value, if (returnValue.value == Operand.Void) it else it.inline(" ")) }
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
            .let { if (definition.inline) it else it.line(";") }
    }

    override fun visit(assignment: Statement.Assignment<*>, out: Indenter): Indenter {
        return visit(assignment.variable, out)
            .inline(" = ")
            .let { visit(assignment.value, it) }
            .let { if (assignment.inline) it else it.line(";") }
    }

    override fun visit(codeBlock: Statement.CodeBlock, out: Indenter): Indenter {
        return out.block(inline = codeBlock.inline) { codeBlock.statements.fold(this) { acc, statement -> visit(statement, acc) } }
    }

    override fun visit(ifStatement: Statement.If, out: Indenter): Indenter {
        return visit(ifStatement.body, out.inline("if (", ") ") { visit(ifStatement.condition, this) })
    }

    override fun visit(elseIfStatement: Statement.ElseIf, out: Indenter): Indenter {
        return visit(elseIfStatement.body, out.inline(" else if (", ") ") { visit(elseIfStatement.condition, this) })
    }

    override fun visit(elseStatement: Statement.Else, out: Indenter): Indenter {
        return out.inline(" else ") { visit(elseStatement.body, this) }
    }

    override fun visit(forStatement: Statement.For, out: Indenter): Indenter {
        return out.inline("for")
            .inline("(", ")") {
                visit(forStatement.init, this)
                    .inline("; ")
                    .let { visit(forStatement.condition, it) }
                    .inline("; ")
                    .let { visit(forStatement.update, it) }
            }.inline(" ") { visit(forStatement.body, this) }
    }

    override fun visit(whileStatement: Statement.While, out: Indenter): Indenter {
        return out.inline("while ")
            .inline("(", ") ") { visit(whileStatement.condition, this) }
            .let { visit(whileStatement.body, it) }
    }

    override fun visit(structDeclaration: StructDeclaration<*>, out: Indenter): Indenter {
        return out.block("struct ${structDeclaration.struct.structName} ", ";") {
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
            .emptyLine()
    }

    override fun visit(breakStatement: Statement.Break, out: Indenter): Indenter {
        return out.line("break;")
    }

    override fun visit(continueStatement: Statement.Continue, out: Indenter): Indenter {
        return out.line("continue;")
    }

    override fun visit(discardStatement: Statement.Discard, out: Indenter): Indenter {
        return out.line("discard;")
    }
}