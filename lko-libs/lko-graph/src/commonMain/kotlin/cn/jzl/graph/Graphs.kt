package cn.jzl.graph

import cn.jzl.graph.impl.AcceptedTypePredicate
import cn.jzl.graph.impl.DefaultGraphNodeInput
import cn.jzl.graph.impl.DefaultGraphNodeOutput
import cn.jzl.graph.impl.MutableNodeConfiguration
import cn.jzl.graph.impl.OutputTypeFunction

fun MutableNodeConfiguration.addNodeOutput(
    fieldId: String,
    fieldName: String,
    connectableFieldTypes: List<String>,
    acceptingMultiple: Boolean = false,
    side: GraphNodeOutputSide = GraphNodeOutputSide.Right,
    required: Boolean = false,
    outputTypeFunction: OutputTypeFunction = OutputTypeFunction
): Unit = addNodeOutput(DefaultGraphNodeOutput(fieldId, fieldName, connectableFieldTypes, acceptingMultiple, side, required, outputTypeFunction))

fun MutableNodeConfiguration.addNodeOutput(
    fieldId: String,
    fieldName: String,
    acceptingMultiple: Boolean = false,
    side: GraphNodeOutputSide = GraphNodeOutputSide.Right,
    required: Boolean = false,
    outputTypeFunction: OutputTypeFunction = OutputTypeFunction,
    vararg producedTypes: String
): Unit = addNodeOutput(fieldId, fieldName, producedTypes.toList(), acceptingMultiple, side, required, outputTypeFunction)

fun MutableNodeConfiguration.addNodeInput(
    fieldId: String,
    fieldName: String,
    acceptingMultiple: Boolean = false,
    side: GraphNodeInputSide = GraphNodeInputSide.Left,
    required: Boolean = false,
    acceptedTypePredicate: AcceptedTypePredicate
): Unit = addNodeInput(DefaultGraphNodeInput(fieldId, fieldName, acceptingMultiple, side, required, acceptedTypePredicate))

fun MutableNodeConfiguration.addNodeInput(
    fieldId: String,
    fieldName: String,
    acceptingMultiple: Boolean = false,
    side: GraphNodeInputSide = GraphNodeInputSide.Left,
    required: Boolean = false,
    vararg fieldTypes: String
): Unit = addNodeInput(fieldId, fieldName, acceptingMultiple, side, required) {
    fieldTypes.isNotEmpty() && it in fieldTypes
}