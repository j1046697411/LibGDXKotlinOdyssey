package cn.jzl.graph.common

class DefaultGraphTypeRegistry : GraphTypeRegistry, GraphTypeResolver {
    private val graphTypes = mutableMapOf<String, GraphType<*>>()

    override fun <PN : PipelineNode, GT : GraphType<PN>> registerGraphTypes(vararg graphTypes: GT) {
        if (graphTypes.isEmpty()) return
        graphTypes.forEach { this.graphTypes[it.type] = it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <PN : PipelineNode> resolve(graphType: String): GraphType<PN> {
        return graphTypes[graphType] as GraphType<PN>
    }
}