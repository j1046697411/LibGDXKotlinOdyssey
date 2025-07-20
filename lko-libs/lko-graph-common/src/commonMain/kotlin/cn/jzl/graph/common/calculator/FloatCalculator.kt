package cn.jzl.graph.common.calculator

internal object FloatCalculator : GeneralCalculator {

    override fun calculate(input: Any, compute: (Float) -> Float): Any {
        require(input is Float)
        return compute(input)
    }

    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        require(first is Float && second is Float)
        return compute(first, second)
    }

    override fun calculate(first: Any, second: Any, third: Any, compute: (Float, Float, Float) -> Float): Any {
        require(first is Float && second is Float && third is Float)
        return compute(first, second, third)
    }

}