package cn.jzl.shader.struct

import cn.jzl.shader.Program
import cn.jzl.shader.VarType
import cn.jzl.shader.VariableDefinition
import cn.jzl.shader.operand.Operand

data class StructDefinition<S : VarType.Struct<S>>(
    val shaderScope: Program.ShaderScope,
    val constructor: (Program.ShaderScope, String) -> S
) : StructConstructor<S> {
    override operator fun invoke(vararg properties: Operand<*>): StructDelegate<S> = StructDelegate { _, property ->
        val struct = constructor(shaderScope, property.name)
        shaderScope.apply {
            statement(VariableDefinition(struct, StructConstructorFunc(struct.structName, properties.toList(), struct)))
        }
        struct
    }
}