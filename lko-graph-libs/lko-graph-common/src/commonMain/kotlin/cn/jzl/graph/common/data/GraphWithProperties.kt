package cn.jzl.graph.common.data

import cn.jzl.graph.Graph

interface GraphWithProperties : Graph {
    val properties: Sequence<GraphProperty>
}