package cn.jzl.graph.shader.core

import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver
import cn.jzl.graph.validator.GraphValidationResult

class DefaultShaderGraphType(
    override val shaderFieldTypeResolver: ShaderFieldTypeResolver,
    override val type: String
) : ShaderGraphType {

    override fun validate(graph: GraphWithProperties): GraphValidationResult {
        TODO("Not yet implemented")
    }
}