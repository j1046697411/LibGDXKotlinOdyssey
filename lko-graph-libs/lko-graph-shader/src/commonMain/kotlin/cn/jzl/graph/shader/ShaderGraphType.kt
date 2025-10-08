package cn.jzl.graph.shader

import cn.jzl.graph.common.GraphType
import cn.jzl.graph.shader.GraphShaderPipelineNode
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver

interface ShaderGraphType : GraphType<GraphShaderPipelineNode> {
    val shaderFieldTypeResolver: ShaderFieldTypeResolver
}