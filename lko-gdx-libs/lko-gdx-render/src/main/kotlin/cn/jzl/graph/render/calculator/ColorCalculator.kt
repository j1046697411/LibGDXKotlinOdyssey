package cn.jzl.graph.render.calculator

import cn.jzl.graph.common.calculator.SingleInputCalculator
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pools

internal object ColorCalculator : SingleInputCalculator {
    override fun calculate(input: Any, compute: (Float) -> Float): Any {
        check(input is Color) { "input must be Color" }
        return Pools.obtain(Color::class.java).set(
            compute(input.r),
            compute(input.g),
            compute(input.b),
            compute(input.a)
        )
    }
}