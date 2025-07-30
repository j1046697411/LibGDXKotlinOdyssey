package cn.jzl.graph.common.producer.general.math

import cn.jzl.graph.common.producer.general.SingleInputGeneralPipelineNodeProducer
import kotlin.math.*

class Arccos : SingleInputGeneralPipelineNodeProducer("Arccos", "Arccos") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = acos(value)
}

class Arcsin : SingleInputGeneralPipelineNodeProducer("Arcsin", "Arcsin") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = asin(value)
}

class Arctan : SingleInputGeneralPipelineNodeProducer("Arctan", "Arctan") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = atan(value)
}

class Cos : SingleInputGeneralPipelineNodeProducer("Cos", "Cos") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = cos(value)
}

class Sin : SingleInputGeneralPipelineNodeProducer("Sin", "Sin") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = sin(value)
}

class Tan : SingleInputGeneralPipelineNodeProducer("Tan", "Tan") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = tan(value)
}

class Degrees : SingleInputGeneralPipelineNodeProducer("Degrees", "Degrees") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = (value * 180f / PI).toFloat()
}

class Radians : SingleInputGeneralPipelineNodeProducer("Radians", "Radians") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = (value * PI / 180f).toFloat()
}