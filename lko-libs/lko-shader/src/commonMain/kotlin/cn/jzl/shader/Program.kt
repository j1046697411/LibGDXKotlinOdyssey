package cn.jzl.shader

interface Program {

    val vertexShader: VertexShader
    val fragmentShader: FragmentShader

    interface Shader {
        val structs: Sequence<StructDeclaration<*>>
        val functions: Sequence<FunctionDeclaration<*>>
        val variableDefinitions: Sequence<PrecisionDefinition<*>>
    }

    interface VertexShader : Shader

    interface FragmentShader : Shader
}