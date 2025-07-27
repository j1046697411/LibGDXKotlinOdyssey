package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pools

internal object ColorToFloatCalculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Color) { "first must be Color" }
        check(second is Float) { "second must be Float" }
        return Pools.obtain(Color::class.java).set(
                compute(first.r, second),
                compute(first.g, second),
                compute(first.b, second),
                compute(first.a, second)
            )
    }
}

