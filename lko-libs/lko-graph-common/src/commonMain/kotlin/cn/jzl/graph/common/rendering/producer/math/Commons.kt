package cn.jzl.graph.common.rendering.producer.math

import cn.jzl.graph.common.rendering.producer.DualInputRenderingPipelineNodeProducer
import cn.jzl.graph.common.rendering.producer.SingleInputRenderingPipelineNodeProducer
import cn.jzl.graph.common.rendering.producer.TripleInputRenderingPipelineNodeProducer
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput
import kotlin.math.*


class Abs : SingleInputRenderingPipelineNodeProducer("abs", "abs") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = abs(value)
}

class Ceiling : SingleInputRenderingPipelineNodeProducer("ceil", "ceiling") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = ceil(value)
}

class Clamp : TripleInputRenderingPipelineNodeProducer("Clamp", "clamp") {
    override val first = createNodeInput("input", "input")
    override val second = createNodeInput("min", "min")
    override val third = createNodeInput("max", "max")
    override val output = createNodeOutput("output", "Result")

    override fun executeFunction(first: Float, second: Float, third: Float): Float = first.coerceIn(second, third)
}

class Floor : SingleInputRenderingPipelineNodeProducer("Floor", "Floor") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = floor(value)
}

class FractionalPart : SingleInputRenderingPipelineNodeProducer("FractionalPart", "FractionalPart") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = value - floor(value)
}

class Lerp : TripleInputRenderingPipelineNodeProducer("Clamp", "clamp") {
    override val first = createNodeInput("form", "Form")
    override val second = createNodeInput("to", "To")
    override val third = createNodeInput("progress", "progress")
    override val output = createNodeOutput("output", "Result")

    override fun executeFunction(first: Float, second: Float, third: Float): Float = first + (second - first) * third
}

class Max : DualInputRenderingPipelineNodeProducer("max", "max") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = max(a, b)
}

class Min : DualInputRenderingPipelineNodeProducer("min", "min") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = min(a, b)
}

class Modulo : DualInputRenderingPipelineNodeProducer("Modulo", "Modulo") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a - b * floor(a / b)
}

class Saturate : SingleInputRenderingPipelineNodeProducer("Saturate", "Saturate") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = value.coerceIn(0f, 1f)
}

class Signum : SingleInputRenderingPipelineNodeProducer("Signum", "Signum") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = when {
        value == 0f -> value
        value >= 0f -> 1f
        else -> -1f
    }
}

class Smoothstep : TripleInputRenderingPipelineNodeProducer("Smoothstep", "Smoothstep") {
    override val first = createNodeInput("input", "input")
    override val second = createNodeInput("edge0", "edge0")
    override val third = createNodeInput("edge1", "edge1")
    override val output = createNodeOutput("output", "Result")

    override fun executeFunction(first: Float, second: Float, third: Float): Float {
        val x = ((first - second) / (third - second)).coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }
}

class Step : DualInputRenderingPipelineNodeProducer("Step", "Step") {
    override val first: NamedGraphNodeInput = createNodeInput("input", "input")
    override val second: NamedGraphNodeInput = createNodeInput("edge", "edge")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = if (a < b) 0f else 1f
}