package cn.jzl.graph.common.producer.general.math

import cn.jzl.graph.common.producer.general.AcceptingMultipleSingleInputGeneralPipelineNodeProducer
import cn.jzl.graph.common.producer.general.DualInputGeneralPipelineNodeProducer
import cn.jzl.graph.common.producer.general.SingleInputGeneralPipelineNodeProducer
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput

class Plus : AcceptingMultipleSingleInputGeneralPipelineNodeProducer("plus", "plus", "Arithmetics/Plus") {
    override val inputs = createNodeInput("inputs", "inputs", acceptingMultiple = true)
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a + b
}

class Minus : DualInputGeneralPipelineNodeProducer("minus", "minus", "Arithmetics/Minus") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a - b
}

class Times : AcceptingMultipleSingleInputGeneralPipelineNodeProducer("times", "times", "Arithmetics/Times") {
    override val inputs = createNodeInput("inputs", "inputs", required = true, acceptingMultiple = true)
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a * b
}

class Div : DualInputGeneralPipelineNodeProducer("div", "div", "Arithmetics/Div") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a / b
}

class Rem : DualInputGeneralPipelineNodeProducer("rem", "rem", "Arithmetics/Rem") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a % b
}

class OneMinus : SingleInputGeneralPipelineNodeProducer("OneMinus", "OneMinus", "Arithmetics/OneMinus") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = 1f - value
}

class Reciprocal : SingleInputGeneralPipelineNodeProducer("Reciprocal", "Reciprocal", "Arithmetics/Reciprocal") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = 1f / value
}