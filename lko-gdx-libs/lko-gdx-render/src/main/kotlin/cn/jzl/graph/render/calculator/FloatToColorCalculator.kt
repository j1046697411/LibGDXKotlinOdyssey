package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import cn.jzl.graph.common.calculator.SingleInputCalculator
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pools

internal object FloatToColorCalculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Float) { "first must be Float" }
        check(second is Color) { "second must be Color" }
        return Pools.obtain(Color::class.java).set(
                compute(first, second.r),
                compute(first, second.g),
                compute(first, second.b),
                compute(first, second.a)
            )
    }
}