package cn.jzl.graph.impl

interface MutableNodeConfiguration : NamedNodeConfiguration {

    fun addNodeInput(input: NamedGraphNodeInput)

    fun addNodeOutput(output: NamedGraphNodeOutput)
}