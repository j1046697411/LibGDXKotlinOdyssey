package cn.jzl.shader

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class SimpleProgram : Program, ProgramScope {
    private val simpleVertexShader = VertexShader()
    private val simpleFragmentShader = FragmentShader()

    override val vertexShader: Program.VertexShader get() = simpleVertexShader

    override val fragmentShader: Program.FragmentShader get() = simpleFragmentShader

    override fun vertexShader(block: ProgramScope.VertexShaderScope.() -> Unit) {
        simpleVertexShader.block()
    }

    override fun fragmentShader(block: ProgramScope.FragmentShaderScope.() -> Unit) {
        simpleFragmentShader.block()
    }

    private abstract class Shader : Program.Shader, ProgramScope.ShaderScope {

        private val statements = mutableListOf<Statement>()
        private val structDeclarations = mutableMapOf<KClass<*>, StructDeclaration<*>>()
        private val functionDeclarations = mutableListOf<FunctionDeclaration<*>>()
        private val precisionDefinitions = mutableMapOf<String, PrecisionDefinition<*>>()

        override val structs: Sequence<StructDeclaration<*>> = structDeclarations.values.asSequence()
        override val variableDefinitions: Sequence<PrecisionDefinition<*>> = precisionDefinitions.values.asSequence().sortedBy { it.typeModifier.ordinal }
        override val functions: Sequence<FunctionDeclaration<*>> = sequence {
            yieldAll(functionDeclarations)
            yield(
                FunctionDeclaration(
                    "main",
                    emptyList(),
                    Statement.CodeBlock(statements.toList()),
                    VarType.Void
                )
            )
        }

        override fun <T : VarType> PrecisionDeclaration<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>> {
            val temporaryVariable = Operand.TemporaryVariable(name, type)
            val precisionDefinition = PrecisionDefinition(typeModifier, temporaryVariable, precision, location, initialValue)
            precisionDefinitions[name] = precisionDefinition
            return VariableProperty(this@Shader, temporaryVariable)
        }

        override fun codeBlock(block: ProgramScope.CodeBlockScope.() -> Unit): Statement.CodeBlock {
            val offset = statements.size
            block()
            val statements = statements.subList(offset, statements.size)
            val codeBlock = Statement.CodeBlock(statements.toList())
            statements.clear()
            return codeBlock
        }

        override fun forStatement(
            min: Operand<VarType.Integer>,
            max: Operand<VarType.Integer>,
            step: Operand<VarType.Integer>,
            block: ProgramScope.ForScope.() -> Unit
        ): Statement.For {
            val loopVar = Operand.TemporaryVariable("loopVar${statements.size}", VarType.Integer)
            val init = Statement.VariableDefinition(loopVar, min, true)
            val condition = loopVar le max
            val update = Statement.Assignment(loopVar, loopVar + step, true)
            val body = codeBlock {
                val forScope = object : ProgramScope.ForScope, ProgramScope.CodeBlockScope by this {
                    override val loopVar: Operand.Variable<VarType.Integer> by VariableProperty(this, loopVar)
                }
                forScope.block()
            }
            return statement(Statement.For(init, condition, update, body))
        }

        @Suppress("UNCHECKED_CAST")
        override fun <R : VarType> func(name: String, block: ProgramScope.FunctionScope<R>.() -> Unit): FunctionDeclaration<R> {
            val args = arrayListOf<Statement.VariableDefinition<*>>()
            var returnType: R? = null
            val body = codeBlock {
                val functionScope = object : ProgramScope.FunctionScope<R>, ProgramScope.CodeBlockScope by this {
                    override fun <T : VarType> ArgDeclaration<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>> {
                        val arg = Arg(property.name, type)
                        args.add(Statement.VariableDefinition(arg, null, true))
                        return VariableProperty(this@Shader, arg)
                    }

                    override fun returnValue(returnValue: Operand<R>): Statement.Return<R> {
                        returnType = returnValue.type
                        return statement(Statement.Return(returnValue))
                    }
                }
                functionScope.block()
            }
            val functionDeclaration = FunctionDeclaration(name, args, body, returnType ?: VarType.Void as R)
            functionDeclarations.add(functionDeclaration)
            return functionDeclaration
        }

        @Suppress("UNCHECKED_CAST")
        override fun <S : Struct<S>> S.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<S, S> {
            val structDeclaration = structDeclarations.getValue(this::class) as StructDeclaration<S>
            return createStructProperty(structDeclaration, map { it.swizzle }.toList(), property)
        }

        @Suppress("UNCHECKED_CAST")
        override fun <S : Struct<S>> StructConstructor<S>.invoke(vararg properties: Operand<*>): StructDeclaration<S> {
            return structDeclarations.getOrPut(this.structType) {
                StructDeclaration(
                    this.structType,
                    factory(this@Shader, "Declaration"),
                    this,
                    properties.toList()
                )
            } as StructDeclaration<S>
        }

        override fun <S : Struct<S>> StructDeclaration<S>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<S, S> {
            return createStructProperty(this, defaultProperties, property)
        }

        @Suppress("CAST_NEVER_SUCCEEDS", "UNCHECKED_CAST")
        private fun <S : Struct<S>> createStructProperty(
            structDeclaration: StructDeclaration<S>,
            properties: List<Operand<*>>,
            property: KProperty<*>
        ): Property<S, S> {
            val struct = structDeclaration.constructor.factory(this, property.name)
            statement(Statement.VariableDefinition(struct, null, false))
            struct.zip(properties.asSequence()) { field, value ->
                val variable = field.swizzle as Operand<VarType>
                Statement.Assignment(variable, value as Operand<VarType>)
            }.forEach { statement(it) }
            return VariableProperty(this, struct)
        }

        override fun <S : Statement> statement(statement: S): S = statement.apply { statements.add(this) }
    }

    private class VertexShader : Shader(), Program.VertexShader, ProgramScope.VertexShaderScope {
        override val glVertexID: Operand<VarType.Integer> = Operand.TemporaryVariable("gl_VertexID", VarType.Integer)
        override val glInstanceID: Operand<VarType.Integer> = Operand.TemporaryVariable("gl_InstanceID", VarType.Integer)
        override var glPosition: Operand<VarType.Vec4> by VariableProperty(this, Operand.TemporaryVariable("gl_Position", VarType.Vec4))
        override var glPointSize: Operand<VarType.Float> by VariableProperty(this, Operand.TemporaryVariable("gl_PointSize", VarType.Float))
    }

    private class FragmentShader : Shader(), Program.FragmentShader, ProgramScope.FragmentShaderScope {
        override val glFragCoord: Operand<VarType.Vec4> = Operand.TemporaryVariable("gl_FragCoord", VarType.Vec4)
        override val glFrontFacing: Operand<VarType.Boolean> = Operand.TemporaryVariable("gl_FrontFacing", VarType.Boolean)
        override val glPointCoord: Operand<VarType.Vec2> = Operand.TemporaryVariable("gl_PointCoord", VarType.Vec2)
        override var glFragColor: Operand<VarType.Vec4> by VariableProperty(this, Operand.TemporaryVariable("gl_FragColor", VarType.Vec4))
        override val glFragDepth: Operand<VarType.Float> = Operand.TemporaryVariable("gl_FragDepth", VarType.Float)
    }
}