package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.TripleInputCalculator
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pools

internal object ColorToFloatTripleInputCalculator : TripleInputCalculator {
    override fun calculate(first: Any, second: Any, third: Any, compute: (Float, Float, Float) -> Float): Any {
        check(first is Color) { "first must be Color" }
        check(second is Float) { "second must be Float" }
        check(third is Float) { "third must be Float" }
        return Pools.obtain(Color::class.java).set(
                compute(first.r, second, third),
                compute(first.g, second, third),
                compute(first.b, second, third),
                compute(first.a, second, third)
            )
    }
}