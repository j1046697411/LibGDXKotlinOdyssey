package cn.jzl.graph

interface GraphNodeIO {

    val fieldId: String

    val required: Boolean

    val acceptingMultiple: Boolean
}