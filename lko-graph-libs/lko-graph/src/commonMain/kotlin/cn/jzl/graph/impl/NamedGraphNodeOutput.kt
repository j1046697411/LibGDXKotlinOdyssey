package cn.jzl.graph.impl

import cn.jzl.graph.GraphNodeOutput

interface NamedGraphNodeOutput : GraphNodeOutput {
    val fieldName: String
}

