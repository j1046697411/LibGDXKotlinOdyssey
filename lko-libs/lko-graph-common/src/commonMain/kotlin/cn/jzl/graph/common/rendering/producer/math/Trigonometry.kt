package cn.jzl.graph.common.rendering.producer.math

import cn.jzl.graph.common.rendering.producer.SingleInputRenderingPipelineNodeProducer
import kotlin.math.*

class Arccos : SingleInputRenderingPipelineNodeProducer("Arccos", "Arccos") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = acos(value)
}

class Arcsin : SingleInputRenderingPipelineNodeProducer("Arcsin", "Arcsin") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = asin(value)
}

class Arctan : SingleInputRenderingPipelineNodeProducer("Arctan", "Arctan") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = atan(value)
}

class Cos : SingleInputRenderingPipelineNodeProducer("Cos", "Cos") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = cos(value)
}

class Sin : SingleInputRenderingPipelineNodeProducer("Sin", "Sin") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = sin(value)
}

class Tan : SingleInputRenderingPipelineNodeProducer("Tan", "Tan") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = tan(value)
}

class Degrees : SingleInputRenderingPipelineNodeProducer("Degrees", "Degrees") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = (value * 180f / PI).toFloat()
}

class Radians : SingleInputRenderingPipelineNodeProducer("Radians", "Radians") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = (value * PI / 180f).toFloat()
}