package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.TripleInputCalculator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pools

internal object Vector2ToFloatTripleInputCalculator : TripleInputCalculator {
    override fun calculate(first: Any, second: Any, third: Any, compute: (Float, Float, Float) -> Float): Any {
        check(first is Vector2) { "first must be Vector2" }
        check(second is Float) { "second must be Float" }
        check(third is Float) { "third must be Float" }
        val vector2 = Pools.obtain(Vector2::class.java)
        vector2.set(compute(first.x, second, third), compute(first.y, second, third))
        return vector2
    }
}