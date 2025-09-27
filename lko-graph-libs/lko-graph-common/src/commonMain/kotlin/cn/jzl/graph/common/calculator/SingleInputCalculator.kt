package cn.jzl.graph.common.calculator

interface SingleInputCalculator : Calculator {
    fun calculate(input: Any, compute: (Float) -> Float): Any
}