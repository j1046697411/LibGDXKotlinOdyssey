package cn.jzl.graph

interface GraphNodeOutput : GraphNodeIO {
    val side: GraphNodeOutputSide
    val connectableFieldTypes: List<String>
    fun determineFieldType(
        configuration: NodeConfiguration,
        graphNode: GraphNode,
        inputs: Map<String, List<String>>
    ): String
}