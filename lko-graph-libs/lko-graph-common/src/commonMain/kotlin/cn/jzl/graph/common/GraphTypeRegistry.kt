package cn.jzl.graph.common

interface GraphTypeRegistry {
    fun <PN : PipelineNode, GT : GraphType<PN>> registerGraphTypes(vararg graphTypes: GT)
}