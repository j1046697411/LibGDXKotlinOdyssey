package cn.jzl.graph.shader.builder.core

import cn.jzl.di.instance
import cn.jzl.ecs.World
import cn.jzl.graph.GraphNode
import cn.jzl.graph.GraphNodeInputSide
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput
import cn.jzl.graph.common.config.GraphPipelineConfiguration
import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.common.producer.AbstractPipelineNodeProducer
import cn.jzl.graph.render.field.ColorType
import cn.jzl.graph.render.field.Vector2Type
import cn.jzl.graph.render.field.Vector3Type
import cn.jzl.graph.render.field.Vector4Type
import cn.jzl.graph.shader.builder.GraphShaderPipelineNode
import cn.jzl.graph.shader.core.ShaderGraphType
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.field.ShaderFieldTypeResolver

class EndModelShaderNodeProducer :
    AbstractPipelineNodeProducer<GraphShaderPipelineNode, ShaderGraphType>("Shader output", "ShaderEnd") {
    private val position = createNodeInput(
        fieldId = "position",
        fieldName = "position",
        required = true,
        side = GraphNodeInputSide.Left,
        fieldTypes = arrayOf(Vector3Type)
    )

    private val color = createNodeInput(
        fieldId = "color",
        fieldName = "color",
        required = true,
        side = GraphNodeInputSide.Left,
        fieldTypes = arrayOf(PrimitiveFieldTypes.FloatFieldType, Vector2Type, Vector3Type, Vector4Type, ColorType)
    )

    private val alpha = createNodeInput(
        fieldId = "alpha",
        fieldName = "alpha",
        required = false,
        side = GraphNodeInputSide.Left,
        fieldTypes = arrayOf(PrimitiveFieldTypes.FloatFieldType)
    )

    private val discardValue = createNodeInput(
        fieldId = "discardValue",
        fieldName = "Discard",
        required = false,
        side = GraphNodeInputSide.Left,
        fieldTypes = arrayOf(PrimitiveFieldTypes.FloatFieldType)
    )

    override fun createNode(
        world: World,
        graph: GraphWithProperties,
        configuration: GraphPipelineConfiguration,
        graphType: ShaderGraphType,
        graphNode: GraphNode,
        inputs: List<PipelineNodeInput>,
        outputs: Map<String, PipelineNodeOutput>
    ): GraphShaderPipelineNode {
        val position = inputs.firstOrNull { it.input == position }
        val color = inputs.firstOrNull { it.input == color }
        val alpha = inputs.firstOrNull { it.input == alpha }
        val discardValue = inputs.firstOrNull { it.input == discardValue }
        val shaderFieldTypeResolver by world.instance<ShaderFieldTypeResolver>()
        return GraphShaderPipelineNode { blackboard, vertexShaderBuilder, fragmentShaderBuilder, fragmentShader ->
            if (fragmentShader) {
                checkNotNull(color) { "color is null" }
                fragmentShaderBuilder.addMainLine("// ${this.configuration.type} Graph node")
                discardValue?.let { shaderFieldTypeResolver.resolve<FieldOutput>(it.outputType) }
                    ?.let { blackboard[discardValue.fromGraphNode, discardValue.fromOutput, it] }
                    ?.let { fragmentShaderBuilder.addMainLine("if (${it.representation} >= 0.5) discard;") }
                val alphaRepresentation = alpha?.let { shaderFieldTypeResolver.resolve<FieldOutput>(it.outputType) }
                    ?.let { blackboard[alpha.fromGraphNode, alpha.fromOutput, it] }?.representation ?: "1.0"
                color.let { shaderFieldTypeResolver.resolve<FieldOutput>(it.outputType) }
                    .let { blackboard[color.fromGraphNode, color.fromOutput, it] }
                    .let {
                        when (it.fieldType.realFieldType) {
                            PrimitiveFieldTypes.FloatFieldType -> {
                                fragmentShaderBuilder.addMainLine("gl_FragColor = vec4(vec3(${it.representation}), $alphaRepresentation);")
                            }

                            Vector2Type -> {
                                fragmentShaderBuilder.addMainLine("gl_FragColor = vec4(${it.representation}, 0.0, $alphaRepresentation);")
                            }

                            Vector3Type -> {
                                fragmentShaderBuilder.addMainLine("gl_FragColor = vec4(${it.representation}, $alphaRepresentation);")
                            }

                            Vector4Type, ColorType -> {
                                if (alpha != null) {
                                    fragmentShaderBuilder.addMainLine("gl_FragColor = ${it.representation};")
                                } else {
                                    fragmentShaderBuilder.addMainLine("gl_FragColor = vec4(${it.representation}.rgb, $alphaRepresentation);")
                                }
                            }
                        }
                    }
            } else {
                checkNotNull(position) { "position is null" }
                position.let { shaderFieldTypeResolver.resolve<FieldOutput>(it.outputType) }
                    .let { blackboard[position.fromGraphNode, position.fromOutput, it] }
                    .let {
                        vertexShaderBuilder.addMainLine("// ${this.configuration.type} Graph node")
                        vertexShaderBuilder.addMainLine("gl_Position = vec4(${it.representation}, 1.0);")
                        vertexShaderBuilder.addVariable("v_position", it.fieldType.fieldType, true)
                    }
            }
        }
    }
}