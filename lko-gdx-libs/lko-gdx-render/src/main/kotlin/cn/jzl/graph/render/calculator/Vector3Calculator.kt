package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.SingleInputCalculator
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Pools

internal object Vector3Calculator : SingleInputCalculator {
    override fun calculate(input: Any, compute: (Float) -> Float): Any {
        check(input is Vector3) { "input must be Vector3" }
        return Pools.obtain(Vector3::class.java).set(compute(input.x), compute(input.y), compute(input.z))
    }
}