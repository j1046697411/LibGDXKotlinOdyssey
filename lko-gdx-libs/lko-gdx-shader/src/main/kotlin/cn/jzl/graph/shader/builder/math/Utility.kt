package cn.jzl.graph.shader.builder.math

import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput
import cn.jzl.graph.shader.core.CommonShaderBuilder
import cn.jzl.graph.shader.field.FieldOutput
import cn.jzl.graph.shader.builder.TripleInputShaderPipelineNodeProducer

class DistanceFromPlane : TripleInputShaderPipelineNodeProducer("DistanceFromPlane", "DistanceFromPlane", "Utils/DistanceFromPlane") {

    override val first: NamedGraphNodeInput = createNodeInput("point", "Point", required = true)
    override val second: NamedGraphNodeInput = createNodeInput("planeNormal", "PlaneNormal", required = true)
    override val third: NamedGraphNodeInput = createNodeInput("planePoint", "PlanePoint", required = true)
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Distance")

    override fun buildFragmentNodeTripleInputs(
        commonShaderBuilder: CommonShaderBuilder,
        firstFieldOutput: FieldOutput,
        secondFieldOutput: FieldOutput,
        thirdFieldOutput: FieldOutput
    ): String = "dot(normalize(${secondFieldOutput.representation}), ${firstFieldOutput.representation} - ${thirdFieldOutput.representation})"
}
