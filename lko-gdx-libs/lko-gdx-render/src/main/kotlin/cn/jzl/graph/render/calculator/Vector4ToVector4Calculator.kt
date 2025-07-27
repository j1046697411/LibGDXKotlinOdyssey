package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.utils.Pools

internal object Vector4ToVector4Calculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Vector4) { "first must be Vector4" }
        check(second is Vector4) { "second must be Vector4" }
        return Pools.obtain(Vector4::class.java).set(
            compute(first.x, second.x),
            compute(first.y, second.y),
            compute(first.z, second.z),
            compute(first.w, second.w)
        )
    }
}