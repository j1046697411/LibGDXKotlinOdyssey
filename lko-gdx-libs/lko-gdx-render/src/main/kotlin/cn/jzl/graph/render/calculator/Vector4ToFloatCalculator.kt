package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.utils.Pools

internal object Vector4ToFloatCalculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Vector4) { "first must be Vector4" }
        check(second is Float) { "second must be Float" }
        return Pools.obtain(Vector4::class.java)
            .set(compute(first.x, second), compute(first.y, second), compute(first.z, second), compute(first.w, second))
    }
}