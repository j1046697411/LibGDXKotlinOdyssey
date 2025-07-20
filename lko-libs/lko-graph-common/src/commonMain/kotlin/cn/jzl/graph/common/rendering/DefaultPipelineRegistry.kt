package cn.jzl.graph.common.rendering

import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeProducer
import cn.jzl.graph.common.PipelineNodeProducerResolver
import cn.jzl.graph.common.calculator.*
import cn.jzl.graph.common.field.FieldType
import cn.jzl.graph.common.field.PipelineFieldTypeResolver
import cn.jzl.graph.common.field.PrimitiveFieldTypes
import kotlin.reflect.KClass

internal class DefaultPipelineRegistry() : PipelineRegistry,
    CalculatorRegistry, PipelineNodeProducerResolver<RenderingPipelineNode>,
    PipelineFieldTypeResolver, GeneralCalculator {

    private val generalCalculator = DefaultGeneralCalculator()
    private val producers = mutableMapOf<String, PipelineNodeProducer<out RenderingPipelineNode>>()
    private val fieldTypes = hashMapOf<String, FieldType<out Any>>()

    override fun calculate(input: Any, compute: (Float) -> Float): Any {
        return generalCalculator.calculate(input, compute)
    }

    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        return generalCalculator.calculate(first, second, compute)
    }

    override fun calculate(first: Any, second: Any, third: Any, compute: (Float, Float, Float) -> Float): Any {
        return generalCalculator.calculate(first, second, third, compute)
    }

    override fun resolve(fieldType: String): FieldType<out Any> {
        return requireNotNull(fieldTypes[fieldType])
    }

    override fun addDefaultFieldType(fieldType: FieldType<out Any>) {
        PrimitiveFieldTypes.addDefaultFieldType(fieldType)
    }

    override fun register(vararg fieldTypes: FieldType<out Any>) {
        if (fieldTypes.isEmpty()) return
        fieldTypes.forEach { fieldType -> this.fieldTypes[fieldType.fieldType] = fieldType }
    }

    override fun register(producer: PipelineNodeProducer<out RenderingPipelineNode>) {
        producers[producer.configuration.type] = producer
    }

    override fun resolve(graphNode: GraphNode): PipelineNodeProducer<RenderingPipelineNode> {
        return requireNotNull(producers[graphNode.type]) as PipelineNodeProducer<RenderingPipelineNode>
    }

    override fun register(type: KClass<*>, singleInputCalculator: SingleInputCalculator) {
        generalCalculator.register(type, singleInputCalculator)
    }

    override fun register(type1: KClass<*>, type2: KClass<*>, dualInputCalculator: DualInputCalculator) {
        generalCalculator.register(type1, type2, dualInputCalculator)
    }

    override fun register(type1: KClass<*>, type2: KClass<*>, type3: KClass<*>, tripleInputCalculator: TripleInputCalculator) {
        generalCalculator.register(type1, type2, type3, tripleInputCalculator)
    }
}