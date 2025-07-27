package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.TripleInputCalculator
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.utils.Pools

internal object Vector4ToFloatTripleInputCalculator : TripleInputCalculator {
    override fun calculate(first: Any, second: Any, third: Any, compute: (Float, Float, Float) -> Float): Any {
        check(first is Vector4) { "first must be Vector4" }
        check(second is Float) { "second must be Float" }
        check(third is Float) { "third must be Float" }
        val vector4 = Pools.obtain(Vector4::class.java)
        return vector4.set(
            compute(first.x, second, third),
            compute(first.y, second, third),
            compute(first.z, second, third),
            compute(first.w, second, third)
        )
    }
}