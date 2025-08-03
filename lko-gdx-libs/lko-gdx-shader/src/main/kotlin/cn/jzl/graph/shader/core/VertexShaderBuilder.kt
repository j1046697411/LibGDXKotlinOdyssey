package cn.jzl.graph.shader.core

import ktx.collections.contains
import ktx.collections.isNotEmpty
import ktx.collections.set

class VertexShaderBuilder(uniformRegistry: UniformRegistry) : CommonShaderBuilder(uniformRegistry) {

    private val attributes = ktx.collections.GdxMap<String, Attribute>()

    fun addAttribute(name: String, componentCount: Int, type: String, comment: String) {
        if (name in attributes) {
            throw IllegalArgumentException("Already contains vertex attribute of that name with different type")
        }
        attributes[name] = Attribute(name, type, componentCount, comment)
        uniformRegistry.registerAttribute(name, componentCount)
    }

    override fun appendAttributeVariables(builder: StringBuilder) {
        if (attributes.isNotEmpty()) {
            attributes.forEach {
                val attribute = it.value
                if (attribute.comment.isNotEmpty()) {
                    builder.append("// ").append(attribute.comment).appendLine()
                }
                builder.append("attribute ${attribute.type} ${attribute.name};").appendLine()
            }
            builder.appendLine()
        }
    }

    data class Attribute(
        val name: String,
        val type: String,
        val componentCount: Int,
        val comment: String
    )
}