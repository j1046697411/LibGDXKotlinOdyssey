package cn.jzl.graph.common.config

import cn.jzl.graph.impl.DefaultNodeConfiguration
import cn.jzl.graph.impl.MutableNodeConfiguration
import cn.jzl.graph.impl.NamedGraphNodeInput
import cn.jzl.graph.impl.NamedGraphNodeOutput
import cn.jzl.graph.impl.NamedNodeConfiguration

class DefaultMenuNodeConfiguration(
    name: String,
    type: String,
    override val menuLocation: String
) : NamedNodeConfiguration, MenuNodeConfiguration, MutableNodeConfiguration {

    private val defaultNodeConfiguration = DefaultNodeConfiguration(name, type)
    override val name: String get() = defaultNodeConfiguration.name
    override val type: String get() = defaultNodeConfiguration.type
    override val nodeInputs: List<NamedGraphNodeInput> get() = defaultNodeConfiguration.nodeInputs
    override val nodeOutputs: List<NamedGraphNodeOutput> get() = defaultNodeConfiguration.nodeOutputs
    override fun addNodeInput(input: NamedGraphNodeInput) {
        defaultNodeConfiguration.addNodeInput(input)
    }
    override fun addNodeOutput(output: NamedGraphNodeOutput) {
        defaultNodeConfiguration.addNodeOutput(output)
    }
}