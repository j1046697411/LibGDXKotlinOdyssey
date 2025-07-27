package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.DualInputCalculator
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pools

internal object FloatToVector3Calculator : DualInputCalculator {
    override fun calculate(first: Any, second: Any, compute: (Float, Float) -> Float): Any {
        check(first is Float) { "first must be Float" }
        check(second is Vector3) { "second must be Vector3" }
        return Pools.obtain(Vector3::class.java).set(compute(first, second.x), compute(first, second.y), compute(first, second.z))
    }
}