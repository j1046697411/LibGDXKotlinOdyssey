package cn.jzl.shader

import kotlin.reflect.KProperty

interface ProgramScope {

    fun vertexShader(block: VertexShaderScope.() -> Unit)

    fun fragmentShader(block: FragmentShaderScope.() -> Unit)


    interface LoopScope : CodeBlockScope {
        fun breakStatement() {
            statement(Statement.Break)
        }

        fun continueStatement() {
            statement(Statement.Continue)
        }
    }

    interface ForScope : CodeBlockScope, LoopScope {
        val loopVar: Operand.Variable<VarType.Integer>
    }

    interface CodeBlockScope : StatementScope, ExpressionScope, PrimitiveScope, VectorScope {

        fun codeBlock(block: CodeBlockScope.() -> Unit): Statement.CodeBlock

        fun ifStatement(condition: Operand<VarType.Boolean>, block: CodeBlockScope.() -> Unit): Statement.If {
            return statement(Statement.If(condition, codeBlock(block)))
        }

        fun Statement.BeforeElse.elseIfStatement(condition: Operand<VarType.Boolean>, block: CodeBlockScope.() -> Unit): Statement.ElseIf {
            inline = true
            return statement(Statement.ElseIf(condition, codeBlock(block)))
        }

        infix fun Statement.BeforeElse.elseStatement(block: CodeBlockScope.() -> Unit): Statement.Else {
            inline = true
            return statement(Statement.Else(codeBlock(block)))
        }

        fun forStatement(
            min: Operand<VarType.Integer>,
            max: Operand<VarType.Integer>,
            step: Operand<VarType.Integer> = 1.lit,
            block: ForScope.() -> Unit
        ): Statement.For

        fun whileStatement(condition: Operand<VarType.Boolean>, block: LoopScope.() -> Unit): Statement.While {
            val codeBlock = codeBlock {
                val loopScope = object : LoopScope, CodeBlockScope by this {}
                loopScope.block()
            }
            return statement(Statement.While(condition, codeBlock))
        }

        override fun <T : VarType> Operand<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>> = property(property.name)

        fun <T : VarType> Operand<T>.property(name: String): Property<T, Operand<T>> {
            val temporaryVariable = Operand.TemporaryVariable(name, type)
            statement(Statement.VariableDefinition(temporaryVariable, this, false))
            return VariableProperty(this@CodeBlockScope, temporaryVariable)
        }

        fun <S : Struct<S>> S.property(name: String): Property<S, S>

        operator fun <S : Struct<S>> S.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<S, S> = property(property.name)

        operator fun <S : Struct<S>> StructConstructor<S>.invoke(vararg properties: Operand<*>): StructDeclaration<S>

        operator fun <S : Struct<S>> StructDeclaration<S>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<S, S>
    }


    interface FunctionScope<R : VarType> : CodeBlockScope {

        val <T : VarType> T.arg: ArgDeclaration<T> get() = ArgDeclaration(this)
        val <S : Struct<S>> S.arg: ArgStruct<S> get() = ArgStruct(this)

        fun returnValue(returnValue: Operand<R>): Statement.Return<R>

        operator fun <T : VarType> ArgDeclaration<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>>
        operator fun <S : Struct<S>> ArgStruct<S>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<S, S>
    }


    interface ShaderScope : CodeBlockScope {

        fun <T : VarType> T.varying(name: String, precision: Precision = Precision.Default, location: Int = -1): PrecisionDeclaration<T> {
            return PrecisionDeclaration(name, this, TypeModifier.Varying, precision, location)
        }

        fun <T : VarType> T.uniform(name: String, precision: Precision = Precision.Default, location: Int = -1): PrecisionDeclaration<T> {
            return PrecisionDeclaration(name, this, TypeModifier.Uniform, precision, location)
        }
    }

    interface VertexShaderScope : ShaderScope {
        val glVertexID: Operand<VarType.Integer>
        val glInstanceID: Operand<VarType.Integer>

        var glPosition: Operand<VarType.Vec4>
        var glPointSize: Operand<VarType.Float>

        fun <T : VarType> T.attribute(name: String, precision: Precision = Precision.Default, location: Int = -1): PrecisionDeclaration<T> {
            return PrecisionDeclaration(name, this, TypeModifier.Attribute, precision, location)
        }
    }


    interface FragmentShaderScope : ShaderScope {
        val glFragCoord: Operand<VarType.Vec4>
        val glFrontFacing: Operand<VarType.Boolean>
        val glPointCoord: Operand<VarType.Vec2>

        var glFragColor: Operand<VarType.Vec4>
        val glFragDepth: Operand<VarType.Float>

        fun discardStatement() {
            statement(Statement.Discard)
        }
    }
}