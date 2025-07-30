package cn.jzl.graph.common.producer.general.math

import cn.jzl.graph.common.producer.general.DualInputGeneralPipelineNodeProducer
import cn.jzl.graph.common.producer.general.SingleInputGeneralPipelineNodeProducer
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput
import kotlin.math.*

class ExponentialBase2 : SingleInputGeneralPipelineNodeProducer("ExponentialBase2", "ExponentialBase2") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = 2f.pow(value)
}

class Exponential : SingleInputGeneralPipelineNodeProducer("Exponential", "Exponential") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = exp(value)
}

class InverseSqrt : SingleInputGeneralPipelineNodeProducer("InverseSqrt", "InverseSqrt") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = 1f / sqrt(value)
}

class LogarithmBase2 : SingleInputGeneralPipelineNodeProducer("LogarithmBase2", "LogarithmBase2") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = log2(value)
}

class NaturalLogarithm : SingleInputGeneralPipelineNodeProducer("NaturalLogarithm", "NaturalLogarithm") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = ln(value)
}

class Power : DualInputGeneralPipelineNodeProducer("Power", "Power") {
    override val first: NamedGraphNodeInput = createNodeInput("a", "A")
    override val second: NamedGraphNodeInput = createNodeInput("b", "B")
    override val output: NamedGraphNodeOutput = createNodeOutput("output", "Result")
    override fun executeFunction(a: Float, b: Float): Float = a.pow(b)
}

class Square : SingleInputGeneralPipelineNodeProducer("Square", "Square") {
    override val input = createNodeInput("input", "input")
    override val output = createNodeOutput("output", "Result")
    override fun executeFunction(value: Float): Float = sqrt(value)
}