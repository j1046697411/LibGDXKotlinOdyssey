package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.utils.Pools

internal object FloatToVector4Calculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Float) { "first must be Float" }
        check(second is Vector4) { "second must be Vector4" }
        return Pools.obtain(Vector4::class.java).set(compute(first, second.x), compute(first, second.y), compute(first, second.z), compute(first, second.w))
    }
}