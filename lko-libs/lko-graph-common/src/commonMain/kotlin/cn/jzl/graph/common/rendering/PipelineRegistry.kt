package cn.jzl.graph.common.rendering

import cn.jzl.graph.common.PipelineNodeProducer
import cn.jzl.graph.common.calculator.CalculatorRegistry
import cn.jzl.graph.common.field.FieldType

interface PipelineRegistry : CalculatorRegistry {

    fun register(producer: PipelineNodeProducer<out RenderingPipelineNode>)

    fun register(vararg fieldTypes: FieldType<out Any>)

    fun addDefaultFieldType(fieldType: FieldType<out Any>)
}

