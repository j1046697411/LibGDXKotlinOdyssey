package cn.jzl.graph.common.field

import cn.jzl.graph.impl.AcceptedTypePredicate

class SimpleAcceptedTypePredicate(val fieldTypes: Array<out FieldType<*>>) : AcceptedTypePredicate {
    override fun test(fieldType: String): Boolean {
        return fieldTypes.isNotEmpty() && fieldTypes.any { it.fieldType == fieldType }
    }
}