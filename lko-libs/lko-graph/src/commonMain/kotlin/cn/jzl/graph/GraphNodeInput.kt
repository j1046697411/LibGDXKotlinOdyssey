package cn.jzl.graph

interface GraphNodeInput : GraphNodeIO {
    val side: GraphNodeInputSide
    fun acceptsFieldType(fieldType: String): Boolean
}