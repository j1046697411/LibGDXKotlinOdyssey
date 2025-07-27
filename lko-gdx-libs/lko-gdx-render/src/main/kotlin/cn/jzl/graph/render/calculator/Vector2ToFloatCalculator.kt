package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pools

internal object Vector2ToFloatCalculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Vector2) { "first must be Vector2" }
        check(second is Float) { "second must be Float" }
        return Pools.obtain(Vector2::class.java).set(compute(first.x, second), compute(first.y, second))
    }
}