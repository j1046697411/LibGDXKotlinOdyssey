package cn.jzl.graph.impl

class DefaultNodeConfiguration(
    override val name: String,
    override val type: String
) : NamedNodeConfiguration, MutableNodeConfiguration {
    private val graphNodeInputs = arrayListOf<NamedGraphNodeInput>()
    private val graphNodeOutputs = mutableListOf<NamedGraphNodeOutput>()

    override val nodeInputs: List<NamedGraphNodeInput> get() = graphNodeInputs
    override val nodeOutputs: List<NamedGraphNodeOutput> get() = graphNodeOutputs

    override fun addNodeInput(input: NamedGraphNodeInput) {
        graphNodeInputs.add(input)
    }

    override fun addNodeOutput(output: NamedGraphNodeOutput) {
        graphNodeOutputs.add(output)
    }
}