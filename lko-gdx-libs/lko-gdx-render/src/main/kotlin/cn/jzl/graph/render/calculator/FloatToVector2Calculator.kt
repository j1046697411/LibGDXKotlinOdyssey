package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pools

internal object FloatToVector2Calculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Float) { "first must be Float" }
        check(second is Vector2) { "second must be Vector2" }
        return Pools.obtain(Vector2::class.java).set(compute(first, second.x), compute(first, second.y))
    }
}