package cn.jzl.graph

interface GraphNode {
    val id: String
    val type: String
    val payloads: Map<String, Any>
}