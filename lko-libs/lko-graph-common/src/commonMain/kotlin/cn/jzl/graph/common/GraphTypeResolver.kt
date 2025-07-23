package cn.jzl.graph.common

interface GraphTypeResolver {
    fun <PN : PipelineNode> resolve(graphType: String): GraphType<PN>
}