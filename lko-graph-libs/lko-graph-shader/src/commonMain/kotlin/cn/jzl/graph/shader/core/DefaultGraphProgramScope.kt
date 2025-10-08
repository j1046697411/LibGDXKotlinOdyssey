package cn.jzl.graph.shader.core

import cn.jzl.shader.*

class DefaultGraphProgramScope(
    private val uniformRegistry: UniformRegistry,
    private val programScope: ProgramScope
) : GraphProgramScope, ProgramScope by programScope {

    override fun <T : VarType> T.attribute(alias: String, precision: Precision, location: Int): PrecisionDeclaration<T> {
        val componentCount = if (this is VarType.Composite) elementCount else 1
        uniformRegistry.registerAttribute(alias, componentCount)
        return PrecisionDeclaration(alias, this, TypeModifier.Attribute, precision, location)
    }

    override fun <T : VarType> T.uniform(name: String, global: Boolean, pedantic: Boolean, location: Int, precision: Precision, setter: UniformSetter): PrecisionDeclaration<T> {
        uniformRegistry.registerUniform(name, global, pedantic, setter)
        return PrecisionDeclaration(name, this, TypeModifier.Uniform, precision, location)
    }

    override fun <T : VarType> T.varying(name: String, location: Int, precision: Precision): PrecisionDeclaration<T> {
        return PrecisionDeclaration(name, this, TypeModifier.Varying, precision, location)
    }
}