package cn.jzl.graph.impl

import cn.jzl.graph.GraphNodeInputSide

data class DefaultGraphNodeInput(
    override val fieldId: String,
    override val fieldName: String,
    override val acceptingMultiple: Boolean = false,
    override val side: GraphNodeInputSide = GraphNodeInputSide.Left,
    override val required: Boolean = false,
    private val acceptedTypePredicate: AcceptedTypePredicate
) : NamedGraphNodeInput {
    override fun acceptsFieldType(fieldType: String): Boolean = acceptedTypePredicate.test(fieldType)
}