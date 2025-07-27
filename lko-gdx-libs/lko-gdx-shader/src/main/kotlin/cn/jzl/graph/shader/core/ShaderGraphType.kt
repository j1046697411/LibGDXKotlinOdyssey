package cn.jzl.graph.shader.core

import cn.jzl.graph.common.GraphType
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver
import cn.jzl.graph.shader.producer.GraphShaderPipelineNode

interface ShaderGraphType : GraphType<GraphShaderPipelineNode> {
    val shaderFieldTypeResolver: ShaderFieldTypeResolver
}

