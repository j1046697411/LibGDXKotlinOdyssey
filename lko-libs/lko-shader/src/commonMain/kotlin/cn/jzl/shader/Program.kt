package cn.jzl.shader

interface Program {

    val vertexShader: VertexShader
    val fragmentShader: FragmentShader

    interface Shader {
        val structs: Sequence<StructDeclaration<*>>
        val functions: Sequence<FunctionDeclaration<*>>
    }

    interface VertexShader : Shader

    interface FragmentShader : Shader
}