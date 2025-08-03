package cn.jzl.graph.shader.core

import cn.jzl.graph.shader.field.ShaderFieldTypeResolver

abstract class AbstractShaderGraphType(override val shaderFieldTypeResolver: ShaderFieldTypeResolver, override val type: String) : ShaderGraphType