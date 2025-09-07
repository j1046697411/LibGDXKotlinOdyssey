package cn.jzl.shader

import cn.jzl.shader.struct.StructConstructor
import cn.jzl.shader.struct.StructDefinition
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

abstract class Shader : Program.ShaderScope, Program.Shader {

    private val statements = mutableListOf<Statement>()
    private val structDefinitions = mutableMapOf<KClass<*>, StructDefinition<*>>()
    private val functionDefinitions = mutableListOf<FunctionDefinition<*>>()

    override val structs: Sequence<StructDefinition<*>> = structDefinitions.values.asSequence()
    override val functions: Sequence<FunctionDefinition<*>> = sequence {
        yieldAll(functionDefinitions)
        yield(
            FunctionDefinition(
                "main",
                emptyList(),
                Statement.CodeBlock(statements.toList()),
                VarType.Void
            )
        )
    }

    final override fun <S : Statement> statement(statement: S): S = statement.apply {
        statements.add(statement)
    }

    override fun <R : VarType> func(name: String, block: Program.FunctionScope<R>.() -> Unit): FunctionDefinition<R> {
        return super.func(name, block).apply { functionDefinitions.add(this) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <S : VarType.Struct<S>> struct(type: KClass<S>, constructor: (Program.ShaderScope, String) -> S): StructConstructor<S> {
        return structDefinitions.getOrPut(type) { StructDefinition(this, constructor) } as StructConstructor<S>
    }

    @Suppress("UNCHECKED_CAST")
    final override fun <S : VarType.Struct<S>> S.provideDelegate(thisRef: Any?, property: KProperty<*>): S {
        val structDefinition = structDefinitions.getValue(this::class) as StructDefinition<S>
        return structDefinition(*this.map { it.swizzle }.toList().toTypedArray()).provideDelegate(thisRef, property)
    }

    final override fun codeBlock(block: CodeBlockScope.() -> Unit): Statement.CodeBlock {
        val offset = statements.size
        this.block()
        val statements = statements.subList(offset, statements.size)
        val codeBlock = Statement.CodeBlock(statements.toList())
        statements.clear()
        return codeBlock
    }
}