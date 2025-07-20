package cn.jzl.graph.impl

import cn.jzl.graph.GraphNode

data class DefaultGraphNode(
    override val id: String,
    override val type: String,
    override val payloads: Map<String, Any> = hashMapOf()
) : GraphNode