package cn.jzl.shader

import cn.jzl.shader.Statement
import cn.jzl.shader.Struct
import cn.jzl.shader.StructConstructor
import cn.jzl.shader.StructDeclaration
import cn.jzl.shader.VarType
import cn.jzl.shader.VariableProperty
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

        override val structs: Sequence<StructDeclaration<*>> = structDeclarations.values.asSequence()
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
            val init = Statement.VariableDefinition(loopVar, min)
            val condition = loopVar le max
            val update = Statement.Assignment(loopVar, loopVar + step)
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
            val args = arrayListOf<Statement.ArgDefinition<*>>()
            var returnType: R? = null
            val body = codeBlock {
                val functionScope = object : ProgramScope.FunctionScope<R>, ProgramScope.CodeBlockScope by this {
                    override fun <T : VarType> ArgConstructor<T>.provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T, Operand<T>> {
                        val arg = Arg(property.name, type)
                        args.add(Statement.ArgDefinition(arg, null))
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

        private fun <S : Struct<S>> createStructProperty(
            structDeclaration: StructDeclaration<S>,
            properties: List<Operand<*>>,
            property: KProperty<*>
        ): Property<S, S> {
            val struct = structDeclaration.constructor.factory(this, property.name)
            val initValue = Operand.SystemFunction(struct.structName, structDeclaration.struct, properties)
            statement(Statement.VariableDefinition(struct, initValue))
            return VariableProperty(this, struct)
        }

        override fun <S : Statement> statement(statement: S): S = statement.apply { statements.add(this) }
    }

    private class VertexShader : Shader(), Program.VertexShader, ProgramScope.VertexShaderScope
    private class FragmentShader : Shader(), Program.FragmentShader, ProgramScope.FragmentShaderScope
}