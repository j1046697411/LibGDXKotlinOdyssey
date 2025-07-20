package cn.jzl.graph.common.calculator

import kotlin.reflect.KClass

internal class DefaultGeneralCalculator : GeneralCalculator, CalculatorRegistry {

    private val singleInputCalculators = hashMapOf<KClass<*>, SingleInputCalculator>()
    private val dualInputCalculators = hashMapOf<Pair<KClass<*>, KClass<*>>, DualInputCalculator>()
    private val tripleInputCalculators = hashMapOf<Triple<KClass<*>, KClass<*>, KClass<*>>, TripleInputCalculator>()

    override fun register(type: KClass<*>, singleInputCalculator: SingleInputCalculator) {
        singleInputCalculators[type] = singleInputCalculator
    }

    override fun register(type1: KClass<*>, type2: KClass<*>, dualInputCalculator: DualInputCalculator) {
        dualInputCalculators[type1 to type2] = dualInputCalculator
    }

    override fun register(type1: KClass<*>, type2: KClass<*>, type3: KClass<*>, tripleInputCalculator: TripleInputCalculator) {
        tripleInputCalculators[Triple(type1, type2, type3)] = tripleInputCalculator
    }

    override fun calculate(input: Any, compute: (Float) -> Float): Any {
        val singleInputCalculator = singleInputCalculators[input::class]
        check(singleInputCalculator != null)
        return singleInputCalculator.calculate(input, compute)
    }

    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        val dualInputCalculator = dualInputCalculators[first::class to second::class]
        check(dualInputCalculator != null)
        return dualInputCalculator.calculate(first, second, compute)
    }

    override fun calculate(first: Any, second: Any, third: Any, compute: (Float, Float, Float) -> Float): Any {
        val tripleInputCalculator = tripleInputCalculators[Triple(first::class, second::class, third::class)]
        check(tripleInputCalculator != null)
        return tripleInputCalculator.calculate(first, second, third, compute)
    }
}

