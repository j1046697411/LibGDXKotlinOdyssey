package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pools

internal object ColorToColorCalculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Color) { "first must be Color" }
        check(second is Color) { "second must be Color" }
        return Pools.obtain(Color::class.java).set(
            compute(first.r, second.r),
            compute(first.g, second.g),
            compute(first.b, second.b),
            compute(first.a, second.a)
        )
    }
}