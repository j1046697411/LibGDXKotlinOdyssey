package cn.jzl.lko.geom

import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

const val PI2: Float = 2 * PI.toFloat()
const val DEG2RAD: Float = PI.toFloat() / 180f
const val RAD2DEG: Float = 180f / PI.toFloat()

@JvmInline
value class Angle(val radians: Float) {
    companion object {
        val ZERO: Angle = Angle(0f)
        val QUARTER: Angle = Angle(PI.toFloat() * 0.5f)
        val HALF: Angle = Angle(PI.toFloat())
        val THREE_QUARTER: Angle = Angle(PI2 * 0.75f)
        val FULL: Angle = Angle(PI2)

        fun ratio(ratio: Ratio): Angle = Angle(ratio.value * PI2)
        fun degrees(degrees: Float): Angle = Angle(degrees * DEG2RAD)
        fun normalized(radians: Float): Angle = Angle(radians % PI2)
    }
}

val Float.radians: Angle get() = Angle(this)

inline val Angle.ratio: Ratio get() = Ratio(radians / PI2)
inline val Angle.degrees: Float get() = radians * RAD2DEG
inline val Angle.normalized: Angle get() = Angle(radians % PI2)

inline val Angle.absolute: Angle get() = Angle(radians.absoluteValue)

inline val Angle.cos: Float get() = cos(radians)
inline val Angle.sin: Float get() = sin(radians)
inline val Angle.tan: Float get() = tan(radians)
