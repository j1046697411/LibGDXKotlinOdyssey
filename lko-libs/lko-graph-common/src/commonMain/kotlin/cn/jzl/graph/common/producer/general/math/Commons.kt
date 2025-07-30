package cn.jzl.graph.common.producer.general.math

import cn.jzl.graph.common.producer.general.DualInputGeneralPipelineNodeProducer
import cn.jzl.graph.common.producer.general.SingleInputGeneralPipelineNodeProducer
import cn.jzl.graph.common.producer.general.TripleInputGeneralPipelineNodeProducer
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput
import kotlin.math.*


class Abs : SingleInputGeneralPipelineNodeProducer("abs", "abs") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = abs(value)
}

class Ceiling : SingleInputGeneralPipelineNodeProducer("ceil", "ceiling") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = ceil(value)
}

class Clamp : TripleInputGeneralPipelineNodeProducer("Clamp", "clamp") {
    override val first = createNodeInput("input", "input")
    override val second = createNodeInput("min", "min")
    override val third = createNodeInput("max", "max")
    override val output = createNodeOutput("output", "Result")

    override fun executeFunction(first: Float, second: Float, third: Float): Float = first.coerceIn(second, third)
}

class Floor : SingleInputGeneralPipelineNodeProducer("Floor", "Floor") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = floor(value)
}

class FractionalPart : SingleInputGeneralPipelineNodeProducer("FractionalPart", "FractionalPart") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = value - floor(value)
}

class Lerp : TripleInputGeneralPipelineNodeProducer("Clamp", "clamp") {
    override val first = createNodeInput("form", "Form")
    override val second = createNodeInput("to", "To")
    override val third = createNodeInput("progress", "progress")
    override val output = createNodeOutput("output", "Result")

    override fun executeFunction(first: Float, second: Float, third: Float): Float = first + (second - first) * third
}

class Max : DualInputGeneralPipelineNodeProducer("max", "max") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = max(a, b)
}

class Min : DualInputGeneralPipelineNodeProducer("min", "min") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = min(a, b)
}

class Modulo : DualInputGeneralPipelineNodeProducer("Modulo", "Modulo") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a - b * floor(a / b)
}

class Saturate : SingleInputGeneralPipelineNodeProducer("Saturate", "Saturate") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = value.coerceIn(0f, 1f)
}

class Signum : SingleInputGeneralPipelineNodeProducer("Signum", "Signum") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = when {
        value == 0f -> value
        value >= 0f -> 1f
        else -> -1f
    }
}

class Smoothstep : TripleInputGeneralPipelineNodeProducer("Smoothstep", "Smoothstep") {
    override val first = createNodeInput("input", "input")
    override val second = createNodeInput("edge0", "edge0")
    override val third = createNodeInput("edge1", "edge1")
    override val output = createNodeOutput("output", "Result")

    override fun executeFunction(first: Float, second: Float, third: Float): Float {
        val x = ((first - second) / (third - second)).coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }
}

class Step : DualInputGeneralPipelineNodeProducer("Step", "Step") {
    override val first: NamedGraphNodeInput = createNodeInput("input", "input")
    override val second: NamedGraphNodeInput = createNodeInput("edge", "edge")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = if (a < b) 0f else 1f
}