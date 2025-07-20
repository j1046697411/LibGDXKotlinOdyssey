package cn.jzl.graph.common.calculator

import kotlin.reflect.KClass

interface CalculatorRegistry {
    fun register(type: KClass<*>, singleInputCalculator: SingleInputCalculator)

    fun register(type1: KClass<*>, type2: KClass<*>, dualInputCalculator: DualInputCalculator)

    fun register(type1: KClass<*>, type2: KClass<*>, type3: KClass<*>, tripleInputCalculator: TripleInputCalculator)
}