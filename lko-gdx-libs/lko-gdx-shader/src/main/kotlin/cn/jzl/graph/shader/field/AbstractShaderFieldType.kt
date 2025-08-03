package cn.jzl.graph.shader.field

import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.shader.builder.property.PropertyLocation
import cn.jzl.graph.shader.builder.property.ShaderPropertySource
import cn.jzl.graph.shader.core.FragmentShaderBuilder
import cn.jzl.graph.shader.core.GraphShader
import cn.jzl.graph.shader.core.ShaderContext
import cn.jzl.graph.shader.core.VertexShaderBuilder

abstract class AbstractShaderFieldType<FO : FieldOutput>: ShaderFieldType<FO> {
    override fun addProperty(
        graph: GraphWithProperties,
        graphNode: GraphNode,
        vertexShaderBuilder: VertexShaderBuilder,
        fragmentShaderBuilder: FragmentShaderBuilder,
        fragmentShader: Boolean,
        propertySource: ShaderPropertySource
    ): FieldOutput {
        val commonShaderBuilder = if (fragmentShader) fragmentShaderBuilder else vertexShaderBuilder
        val uniformName = when (propertySource.propertyLocation) {
            PropertyLocation.Uniform -> {
                val name = "u_property_${propertySource.propertyIndex}"
                commonShaderBuilder.addUniformVariable(
                    name,
                    fieldType,
                    false,
                    "${realFieldType.fieldType} property - ${propertySource.propertyName}"
                ) { shaderContext, shader, location -> setModelUniform(shaderContext, shader, location, propertySource) }
                name
            }

            PropertyLocation.GlobalUniform -> {
                val name = "u_property_${propertySource.propertyIndex}"
                commonShaderBuilder.addUniformVariable(
                    name,
                    fieldType,
                    true,
                    "${realFieldType.fieldType} property - ${propertySource.propertyName}"
                ) { shaderContext, shader, location -> setGlobalUniform(shaderContext, shader, location, propertySource) }
                name
            }

            PropertyLocation.Attribute -> {
                val name = "a_property_${propertySource.propertyIndex}"
                vertexShaderBuilder.addAttribute(
                    name,
                    numberOfComponents,
                    fieldType,
                    "${realFieldType.fieldType} property - ${propertySource.propertyName}"
                )
                if (fragmentShader) {
                    val variableName = "v_property_${propertySource.propertyIndex}"
                    vertexShaderBuilder.addVariable(variableName, fieldType, true)
                    fragmentShaderBuilder.addVariable(variableName, fieldType, true)
                    vertexShaderBuilder.addMainLine("$variableName = $name;")
                    variableName
                } else {
                    name
                }
            }
        }
        return DefaultFieldOutput(this, uniformName)
    }

    protected abstract fun setModelUniform(shaderContext: ShaderContext, shader: GraphShader, location: Int, propertySource: ShaderPropertySource)
    protected abstract fun setGlobalUniform(shaderContext: ShaderContext, shader: GraphShader, location: Int, propertySource: ShaderPropertySource)
}