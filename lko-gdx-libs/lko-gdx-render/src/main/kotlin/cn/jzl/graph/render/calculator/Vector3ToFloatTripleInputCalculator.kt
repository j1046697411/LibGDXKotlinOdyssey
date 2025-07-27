package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.TripleInputCalculator
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pools

internal object Vector3ToFloatTripleInputCalculator : TripleInputCalculator {
    override fun calculate(first: Any, second: Any, third: Any, compute: (Float, Float, Float) -> Float): Any {
        check(first is Vector3) { "first must be Vector3" }
        check(second is Float) { "second must be Float" }
        check(third is Float) { "third must be Float" }
        val vector3 = Pools.obtain(Vector3::class.java)
        return vector3.set(
            compute(first.x, second, third),
            compute(first.y, second, third),
            compute(first.z, second, third)
        )
    }
}