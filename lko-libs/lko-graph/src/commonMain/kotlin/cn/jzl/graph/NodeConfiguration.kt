package cn.jzl.graph

interface NodeConfiguration {
    val type: String
    val name: String
    val nodeInputs: List<GraphNodeInput>
    val nodeOutputs: List<GraphNodeOutput>
}