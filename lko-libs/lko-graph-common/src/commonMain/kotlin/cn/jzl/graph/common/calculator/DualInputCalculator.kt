package cn.jzl.graph.common.calculator

interface DualInputCalculator : Calculator {
    fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any
}