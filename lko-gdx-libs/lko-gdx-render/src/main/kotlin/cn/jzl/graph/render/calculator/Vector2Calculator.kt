package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.SingleInputCalculator
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pools

internal object Vector2Calculator : SingleInputCalculator {
    override fun calculate(input: Any, compute: (Float) -> Float): Any {
        check(input is Vector2) { "input must be Vector2" }
        return Pools.obtain(Vector2::class.java).set(compute(input.x), compute(input.y))
    }
}

