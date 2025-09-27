package cn.jzl.graph.impl

import cn.jzl.graph.GraphConnection

data class DefaultGraphConnection(
    override val nodeFrom: String,
    override val fieldFrom: String,
    override val nodeTo: String,
    override val fieldTo: String
) : GraphConnection