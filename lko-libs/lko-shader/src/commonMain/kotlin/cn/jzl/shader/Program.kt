package cn.jzl.shader

import cn.jzl.shader.StructDeclaration

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