package cn.jzl.graph.common.calculator

interface TripleInputCalculator : Calculator {
    fun calculate(first: Any, second: Any, third: Any, compute: (Float, Float, Float) -> Float): Any
}