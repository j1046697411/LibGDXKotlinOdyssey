package cn.jzl.shader

import cn.jzl.shader.struct.StructPropertyDelegate

class SimpleProgram : Program, Program.ProgramScope {

    private val simpleVertexShader = SimpleVertexShader()
    private val simpleFragmentShader = SimpleFragmentShader()

    override val vertexShader: Program.VertexShader get() = simpleVertexShader
    override val fragmentShader: Program.FragmentShader get() = simpleFragmentShader

    override fun <T : VarType, S : VarType.Struct<S>> S.property(type: T): StructPropertyDelegate<T, S> {
        return StructPropertyDelegate { _, property -> getStructProperty(property, type) }
    }

    override fun vertexShader(block: Program.VertexShaderScope.() -> Unit) {
        simpleVertexShader.block()
    }

    override fun fragmentShader(block: Program.FragmentShaderScope.() -> Unit) {
        simpleFragmentShader.block()
    }

    internal class SimpleVertexShader : Shader(), Program.VertexShaderScope, Program.VertexShader

    internal class SimpleFragmentShader : Shader(), Program.FragmentShaderScope, Program.FragmentShader
}