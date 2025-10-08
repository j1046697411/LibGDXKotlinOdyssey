package cn.jzl.graph.shader.core

import cn.jzl.shader.Precision
import cn.jzl.shader.PrecisionDeclaration
import cn.jzl.shader.ProgramScope
import cn.jzl.shader.VarType
import cn.jzl.shader.VarTypeAccessor

interface GraphProgramScope : ProgramScope, VarTypeAccessor {
    override fun fragmentShader(block: ProgramScope.FragmentShaderScope.() -> Unit)
    override fun vertexShader(block: ProgramScope.VertexShaderScope.() -> Unit)

    fun <T : VarType> T.attribute(alias: String, precision: Precision = Precision.Default, location: Int = -1): PrecisionDeclaration<T>

    fun <T : VarType> T.uniform(
        name: String,
        global: Boolean = false,
        pedantic: Boolean = false,
        location: Int = -1,
        precision: Precision = Precision.Default,
        setter: UniformSetter
    ): PrecisionDeclaration<T>

    fun <T : VarType> T.varying(
        name: String,
        location: Int = -1,
        precision: Precision = Precision.Default
    ): PrecisionDeclaration<T>
}