package cn.jzl.graph.common.rendering

import cn.jzl.graph.common.GraphTypeRegistry
import cn.jzl.graph.common.PipelineNodeProducerRegistry
import cn.jzl.graph.common.calculator.CalculatorRegistry
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.field.FieldTypeRegistry

interface PipelineRegistry : CalculatorRegistry, FieldTypeRegistry, PipelineNodeProducerRegistry, GraphTypeRegistry {

    override fun registerFieldTypes(vararg fieldTypes: FieldType<out Any>)

    fun addDefaultFieldType(fieldType: FieldType<out Any>)
}

