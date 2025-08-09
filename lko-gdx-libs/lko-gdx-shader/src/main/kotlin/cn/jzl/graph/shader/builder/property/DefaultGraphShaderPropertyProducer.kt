package cn.jzl.graph.shader.builder.property

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldType
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver

class DefaultGraphShaderPropertyProducer : GraphShaderPropertyProducer {

    override fun createProperty(world: World, graph: GraphWithProperties, graphNode: GraphNode): ShaderPropertySource {
        val shaderFieldTypeResolver by world.instance<ShaderFieldTypeResolver>()
        val fieldType = graphNode.payloads.getValue("fieldType") as FieldType<*>
        val propertyLocation = graphNode.payloads.getValue("propertyLocation") as PropertyLocation
        val propertyName = graphNode.payloads.getValue("propertyName") as String
        val shaderFieldType = shaderFieldTypeResolver.resolve<FieldOutput>(fieldType)
        return DescriptorShaderPropertySource(
            shaderFieldType,
            propertyName,
            propertyLocation,
            null
        )
    }

    private data class DescriptorShaderPropertySource(
        override val shaderFieldType: ShaderFieldType<out FieldOutput>,
        override val propertyName: String,
        override val propertyLocation: PropertyLocation,
        override val attributeFunction: String?
    ) : ShaderPropertySource
}