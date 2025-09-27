package cn.jzl.graph.common.field

import cn.jzl.graph.impl.AcceptedTypePredicate

internal object DefaultAcceptedTypePredicate : AcceptedTypePredicate {

    override fun test(fieldType: String): Boolean {
        return PrimitiveFieldTypes.values().any { it.fieldType == fieldType }
    }
}

