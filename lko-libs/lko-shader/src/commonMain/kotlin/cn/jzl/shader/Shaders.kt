package cn.jzl.shader

import cn.jzl.shader.operand.Func
import cn.jzl.shader.operand.Operand
import cn.jzl.shader.operand.Variable
import cn.jzl.shader.struct.StructConstructor
import cn.jzl.shader.struct.StructDefinition
import cn.jzl.shader.struct.StructPropertyDelegate
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

data class FunctionDefinition<R : VarType>(
    val name: String,
    val args: List<Arg<*>>,
    val body: Statement.CodeBlock,
    val returnType: R
) : Statement

data class CustomFunc<R : VarType>(
    val functionDefinition: FunctionDefinition<R>,
    override val params: List<Operand<*>>
) : Func<R> {
    override val name: String get() = functionDefinition.name
    override val type: R get() = functionDefinition.returnType
}

data class ArgDelegate<T : VarType>(
    val type: T,
    private val block: (Arg<T>) -> Unit
) {
    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Variable<T> {
        return Arg(property.name, type).apply(block)
    }
}

data class Arg<T : VarType>(override val name: String, override val type: T) : Variable<T>

interface Program {

    val vertexShader: VertexShader
    val fragmentShader: FragmentShader

    interface VertexShaderScope : ShaderScope
    interface FragmentShaderScope : ShaderScope

    interface Shader {
        val structs: Sequence<StructDefinition<*>>
        val functions: Sequence<FunctionDefinition<*>>
    }

    interface VertexShader : Shader
    interface FragmentShader : Shader

    fun vertexShader(block: VertexShaderScope.() -> Unit)

    fun fragmentShader(block: FragmentShaderScope.() -> Unit)

    interface ShaderScope : CodeBlockScope {
        fun <R : VarType> func(name: String, block: FunctionScope<R>.() -> Unit): FunctionDefinition<R> {
            val args = mutableListOf<Arg<*>>()
            var returnType: R? = null
            val body = codeBlock {
                val functionScope = object : FunctionScope<R>, CodeBlockScope by this {
                    override fun <T : VarType> arg(type: T): ArgDelegate<T> {
                        return ArgDelegate(type) { args.add(it) }
                    }

                    override fun returnValue(returnValue: Operand<R>): Statement.Return<R> {
                        returnType = returnValue.type
                        return statement(Statement.Return(returnValue))
                    }
                }
                functionScope.block()
            }
            @Suppress("UNCHECKED_CAST")
            return FunctionDefinition(name, args, body, returnType ?: VarType.Void as R)
        }

        fun <S : VarType.Struct<S>> struct(type: KClass<S>, constructor: (ShaderScope, String) -> S): StructConstructor<S>
    }

    interface FunctionScope<R : VarType> : CodeBlockScope {

        fun <T : VarType> arg(type: T): ArgDelegate<T>

        fun returnValue(returnValue: Operand<R>): Statement.Return<R>
    }

    interface ProgramScope {

        fun <T : VarType, S : VarType.Struct<S>> S.property(type: T): StructPropertyDelegate<T, S>

        fun vertexShader(block: VertexShaderScope.() -> Unit)

        fun fragmentShader(block: FragmentShaderScope.() -> Unit)
    }
}


inline fun <reified S : VarType.Struct<S>> Program.ShaderScope.struct(
    noinline constructor: (Program.ShaderScope, String) -> S
): StructConstructor<S> = struct(S::class, constructor)


fun main(args: Array<String>) {
    val program = SimpleProgram()
    program.run {
        class User(shader: Program.ShaderScope, name: String) : VarType.Struct<User>(shader, name) {
            val username by property(VarType.Integer)
            val password by property(VarType.Integer)
            val age by property(VarType.Integer)
            val sex by property(VarType.Integer)
            val email by property(VarType.Integer)
        }
        vertexShader {
            val main = func("user") {
                val a by arg(VarType.Integer)
                val b by arg(VarType.Integer)
                val c by arg(VarType.Integer)
                val user by struct(::User)(a, b, c)
                val user2 by user
                ifBlock(user) {
                    returnValue(user)
                }
                returnValue(user2)
            }
            val user2 by main()
        }

        fragmentShader {
            val user by struct(::User)()

        }

    }
    val glslBodyGenerator = GlslBodyGenerator()
    val glsl = glslBodyGenerator.visit(program, Indenter())
    val text = buildString { evaluate(glsl) { _, _ -> } }
    println(text)
}
