package cn.jzl.graph.shader.producer.util

import cn.jzl.graph.common.field.PrimitiveFieldTypes
import cn.jzl.graph.render.field.ColorType
import cn.jzl.graph.render.field.Vector2Type
import cn.jzl.graph.render.field.Vector3Type
import cn.jzl.graph.render.field.Vector4Type
import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.producer.TripleInputShaderPipelineNodeProducer

class Remap : TripleInputShaderPipelineNodeProducer("Remap", "Remap") {
    override val first = createNodeInput(
        fieldId = "input",
        fieldName = "Input",
        required = true,
        fieldTypes = arrayOf(PrimitiveFieldTypes.FloatFieldType, Vector2Type, Vector3Type, ColorType, Vector4Type)
    )
    override val second = createNodeInput(
        fieldId = "from",
        fieldName = "From",
        required = true,
        fieldTypes = arrayOf(Vector2Type)
    )
    override val third = createNodeInput(
        fieldId = "to",
        fieldName = "To",
        required = true,
        fieldTypes = arrayOf(Vector2Type)
    )

    override val output = createNodeOutput("output", "Output")

    override fun buildFragmentNodeTripleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput,
        thirdFieldOutput: FieldOutput
    ): String = "(${thirdFieldOutput.representation}.x + (${firstFieldOutput.representation} - ${secondFieldOutput.representation}.x) * (${thirdFieldOutput.representation}.y - ${thirdFieldOutput.representation}.x) / (${secondFieldOutput.representation}.y - ${secondFieldOutput.representation}.x))"
}

