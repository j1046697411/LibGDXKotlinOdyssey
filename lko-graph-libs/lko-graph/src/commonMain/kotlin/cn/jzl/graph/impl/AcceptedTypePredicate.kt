package cn.jzl.graph.impl

fun interface AcceptedTypePredicate {
    fun test(fieldType: String): Boolean
}