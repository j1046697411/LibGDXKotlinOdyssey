package cn.jzl.graph.common.rendering.producer.math

import cn.jzl.graph.common.rendering.producer.AcceptingMultipleSingleInputRenderingPipelineNodeProducer
import cn.jzl.graph.common.rendering.producer.DualInputRenderingPipelineNodeProducer
import cn.jzl.graph.common.rendering.producer.SingleInputRenderingPipelineNodeProducer
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput

class Plus : AcceptingMultipleSingleInputRenderingPipelineNodeProducer("plus", "plus") {
    override val inputs = createNodeInput("inputs", "inputs", acceptingMultiple = true)
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a + b
}

class Minus : DualInputRenderingPipelineNodeProducer("minus", "minus") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a - b
}

class Times : AcceptingMultipleSingleInputRenderingPipelineNodeProducer("times", "times") {
    override val inputs = createNodeInput("inputs", "inputs", acceptingMultiple = true)
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a * b
}

class Div : DualInputRenderingPipelineNodeProducer("div", "div") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a / b
}

class Rem : DualInputRenderingPipelineNodeProducer("rem", "rem") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a % b
}

class OneMinus : SingleInputRenderingPipelineNodeProducer("OneMinus", "OneMinus") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = 1f - value
}

class Reciprocal : SingleInputRenderingPipelineNodeProducer("Reciprocal", "Reciprocal") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = 1f / value
}