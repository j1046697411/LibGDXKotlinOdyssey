package cn.jzl.graph.impl

import cn.jzl.graph.GraphNodeInput

interface NamedGraphNodeInput : GraphNodeInput {
    val fieldName: String
}