package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.SingleInputCalculator
import com.badlogic.gdx.math.Vector4
import com.badlogic.gdx.utils.Pools

internal object Vector4Calculator : SingleInputCalculator {
    override fun calculate(input: Any, compute: (Float) -> Float): Any {
        check(input is Vector4) { "input must be Vector4" }
        return Pools.obtain(Vector4::class.java)
            .set(compute(input.x), compute(input.y), compute(input.z), compute(input.w))
    }
}