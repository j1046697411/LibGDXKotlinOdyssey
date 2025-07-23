package cn.jzl.graph.common.rendering

import cn.jzl.graph.common.GraphTypeRegistry
import cn.jzl.graph.common.PipelineNodeProducerRegistry
import cn.jzl.graph.common.calculator.CalculatorRegistry
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.field.FieldTypeRegistry
import cn.jzl.graph.common.field.PrimitiveFieldTypes

internal class DefaultPipelineRegistry(
    private val generalCalculator: CalculatorRegistry,
    private val fieldTypeRegistry: FieldTypeRegistry,
    private val pipelineNodeProducerRegistry: PipelineNodeProducerRegistry,
    private val graphTypeRegistry: GraphTypeRegistry
) : PipelineRegistry, CalculatorRegistry by generalCalculator,
    FieldTypeRegistry by fieldTypeRegistry, PipelineNodeProducerRegistry by pipelineNodeProducerRegistry ,
    GraphTypeRegistry by graphTypeRegistry{
    override fun addDefaultFieldType(fieldType: FieldType<out Any>) {
        PrimitiveFieldTypes.addDefaultFieldType(fieldType)
    }
}