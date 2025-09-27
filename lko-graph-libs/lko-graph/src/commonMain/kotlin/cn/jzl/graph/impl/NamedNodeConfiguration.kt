package cn.jzl.graph.impl

import cn.jzl.graph.NodeConfiguration

interface NamedNodeConfiguration : NodeConfiguration {
    override val nodeInputs: List<NamedGraphNodeInput>
    override val nodeOutputs: List<NamedGraphNodeOutput>
}