package cn.jzl.datastructure.math

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 缓动（Easing）接口：对比例值进行变换以实现不同的动画节奏。
 *
 * - 调用方式：`ratio.ease(Easing.Linear)`
 * - 扩展性：可自定义如 ease-in/ease-out 等曲线
 */
fun interface Easing {
    /**
     * 对输入比例 `ratio` 进行缓动变换，返回新的比例。
     */
    operator fun invoke(ratio: Ratio): Ratio

    /**
     * 线性缓动：不改变比例值。
     */
    data object Linear : Easing {
        override operator fun invoke(ratio: Ratio): Ratio = ratio
    }

    // 常用缓动函数（基于 https://easings.net 的定义），全部收纳于接口内部
    /** 二次缓动：加速 */
    data object EaseInQuad : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(t * t)
        }
    }
    /** 二次缓动：减速 */
    data object EaseOutQuad : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(t * (2f - t))
        }
    }
    /** 二次缓动：先加速后减速 */
    data object EaseInOutQuad : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return if (t < 0.5f) Ratio(2f * t * t) else Ratio(-1f + (4f - 2f * t) * t)
        }
    }

    /** 三次缓动：加速 */
    data object EaseInCubic : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(t * t * t)
        }
    }
    /** 三次缓动：减速 */
    data object EaseOutCubic : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            val u = 1f - t
            return Ratio(1f - u * u * u)
        }
    }
    /** 三次缓动：先加速后减速 */
    data object EaseInOutCubic : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return if (t < 0.5f) Ratio(4f * t * t * t) else Ratio(1f - ((-2f * t + 2f).toDouble().pow(3.0)).toFloat() / 2f)
        }
    }

    /** 正弦缓动：加速 */
    data object EaseInSine : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio((1.0 - cos(t * PI / 2.0)).toFloat())
        }
    }
    /** 正弦缓动：减速 */
    data object EaseOutSine : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(sin(t * PI / 2.0).toFloat())
        }
    }
    /** 正弦缓动：先加速后减速 */
    data object EaseInOutSine : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio((-(cos(PI * t) - 1.0) / 2.0).toFloat())
        }
    }

    /** 指数缓动：加速 */
    data object EaseInExpo : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            if (t == 0f) return Ratio(0f)
            val v = 2.0.pow((10.0 * t - 10.0)).toFloat()
            return Ratio(v)
        }
    }
    /** 指数缓动：减速 */
    data object EaseOutExpo : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            if (t == 1f) return Ratio(1f)
            val v = 1f - 2.0.pow((-10.0 * t)).toFloat()
            return Ratio(v)
        }
    }
    /** 指数缓动：先加速后减速 */
    data object EaseInOutExpo : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            if (t == 0f) return Ratio(0f)
            if (t == 1f) return Ratio(1f)
            return if (t < 0.5f) {
                Ratio((2.0.pow((20.0 * t - 10.0)) / 2.0).toFloat())
            } else {
                val v = ((2.0 - 2.0.pow((-20.0 * t + 10.0))) / 2.0).toFloat()
                return Ratio(v)
            }
        }
    }

    /** 圆形缓动：加速 */
    data object EaseInCirc : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(1f - sqrt(1f - t * t))
        }
    }
    /** 圆形缓动：减速 */
    data object EaseOutCirc : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(sqrt(1f - (t - 1f) * (t - 1f)))
        }
    }
    /** 圆形缓动：先加速后减速 */
    data object EaseInOutCirc : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return if (t < 0.5f) Ratio((1f - sqrt(1f - (2f * t) * (2f * t))) / 2f) else Ratio((sqrt(1f - (-2f * t + 2f) * (-2f * t + 2f)) + 1f) / 2f)
        }
    }

    /** 回弹缓动（Back）：加速 */
    data object EaseInBack : Easing {
        private const val c1 = 1.70158f
        private const val c3 = c1 + 1f
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(c3 * t * t * t - c1 * t * t)
        }
    }
    /** 回弹缓动（Back）：减速 */
    data object EaseOutBack : Easing {
        private const val c1 = 1.70158f
        private const val c3 = c1 + 1f
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value - 1f
            return Ratio(1f + c3 * t * t * t + c1 * t * t)
        }
    }
    /** 回弹缓动（Back）：先加速后减速 */
    data object EaseInOutBack : Easing {
        private const val c1 = 1.70158f
        private const val c2 = c1 * 1.525f
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return if (t < 0.5f) Ratio(((2f * t) * (2f * t) * ((c2 + 1f) * 2f * t - c2)) / 2f) else Ratio((((2f * t - 2f) * (2f * t - 2f)) * ((c2 + 1f) * (2f * t - 2f) + c2) + 2f) / 2f)
        }
    }

    companion object {
        fun bounceOut(tInput: Float): Float {
            val t = tInput
            val n1 = 7.5625f
            val d1 = 2.75f
            return when {
                t < 1f / d1 -> n1 * t * t
                t < 2f / d1 -> {
                    val u = t - 1.5f / d1
                    n1 * u * u + 0.75f
                }
                t < 2.5f / d1 -> {
                    val u = t - 2.25f / d1
                    n1 * u * u + 0.9375f
                }
                else -> {
                    val u = t - 2.625f / d1
                    n1 * u * u + 0.984375f
                }
            }
        }

        /**
         * 参数化 Back 缓动：加速。
         * - `strength` 控制回弹幅度（默认与 Penner 常量一致：1.70158）。
         */
        fun backIn(strength: Float = 1.70158f): Easing = Easing { ratio ->
            val t = ratio.value
            val c1 = strength
            Ratio((c1 + 1f) * t * t * t - c1 * t * t)
        }
        /** 参数化 Back 缓动：减速。 */
        fun backOut(strength: Float = 1.70158f): Easing = Easing { ratio ->
            val t = ratio.value
            val c1 = strength
            val u = 1f - t
            Ratio(1f - ((c1 + 1f) * u * u * u - c1 * u * u))
        }
        /** 参数化 Back 缓动：先加速后减速。 */
        fun backInOut(strength: Float = 1.70158f): Easing = Easing { ratio ->
            val t = ratio.value
            val c2 = strength * 1.525f
            if (t < 0.5f) {
                val x = 2f * t
                Ratio(x * x * ((c2 + 1f) * x - c2) / 2f)
            } else {
                val x = 2f * t - 2f
                Ratio((x * x * ((c2 + 1f) * x + c2) + 2f) / 2f)
            }
        }

        /**
         * 参数化 Elastic 缓动：加速。
         * - `amplitude` 振幅，`period` 周期（默认 0.3，对应经典曲线）。
         */
        fun elasticIn(amplitude: Float = 1f, period: Float = 0.3f): Easing = Easing { ratio ->
            val t = ratio.value
            if (t == 0f || t == 1f) Ratio(t) else {
                val p = period.toDouble()
                val a = amplitude.toDouble()
                val s = p / 4.0
                val v = -(2.0.pow(10.0 * (t.toDouble() - 1.0))) * sin(((t.toDouble() - 1.0) - s) * (2.0 * PI / p))
                Ratio(v.toFloat())
            }
        }
        /** 参数化 Elastic 缓动：减速。 */
        fun elasticOut(amplitude: Float = 1f, period: Float = 0.3f): Easing = Easing { ratio ->
            val t = ratio.value
            if (t == 0f || t == 1f) Ratio(t) else {
                val p = period.toDouble()
                val a = amplitude.toDouble()
                val s = p / 4.0
                val v = 2.0.pow(-10.0 * t.toDouble()) * sin((t.toDouble() - s) * (2.0 * PI / p)) + 1.0
                Ratio(v.toFloat())
            }
        }
        /** 参数化 Elastic 缓动：先加速后减速。 */
        fun elasticInOut(amplitude: Float = 1f, period: Float = 0.45f): Easing = Easing { ratio ->
            val t = ratio.value
            if (t == 0f || t == 1f) Ratio(t) else {
                val p = period.toDouble()
                val a = amplitude.toDouble()
                val s = p / 4.0
                if (t < 0.5f) {
                    val x = 2.0 * t.toDouble() - 1.0
                    val v = -0.5 * 2.0.pow(10.0 * x) * sin((x - s) * (2.0 * PI / p))
                    Ratio(v.toFloat())
                } else {
                    val x = 2.0 * t.toDouble() - 1.0
                    val v = 0.5 * 2.0.pow(-10.0 * x) * sin((x - s) * (2.0 * PI / p)) + 1.0
                    Ratio(v.toFloat())
                }
            }
        }
    }

    /** 弹跳缓动：加速 */
    data object EaseInBounce : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(1f - bounceOut(1f - t))
        }
    }
    /** 弹跳缓动：减速 */
    data object EaseOutBounce : Easing {
        override operator fun invoke(ratio: Ratio): Ratio = Ratio(bounceOut(ratio.value))
    }
    /** 弹跳缓动：先加速后减速 */
    data object EaseInOutBounce : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return if (t < 0.5f) Ratio((1f - bounceOut(1f - 2f * t)) / 2f) else Ratio((1f + bounceOut(2f * t - 1f)) / 2f)
        }
    }

    /** 弹性缓动：加速 */
    data object EaseInElastic : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            if (t == 0f || t == 1f) return Ratio(t)
            val c4 = (2 * PI / 3).toFloat()
            val v = (-2.0).pow((10.0 * t - 10.0)) * sin(((t * 10.0) - 10.75) * c4)
            return Ratio(v.toFloat())
        }
    }
    /** 弹性缓动：减速 */
    data object EaseOutElastic : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            if (t == 0f || t == 1f) return Ratio(t)
            val c4 = (2 * PI / 3).toFloat()
            val v = 2.0.pow((-10.0 * t)) * sin(((t * 10.0) - 0.75) * c4) + 1.0
            return Ratio(v.toFloat())
        }
    }
    /** 弹性缓动：先加速后减速 */
    data object EaseInOutElastic : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            if (t == 0f || t == 1f) return Ratio(t)
            val c5 = (2 * PI / 4.5).toFloat()
            return if (t < 0.5f) {
                val v = -(2.0.pow((20.0 * t - 10.0)) * sin(((20.0 * t) - 11.125) * c5)) / 2.0
                Ratio(v.toFloat())
            } else {
                val v = (2.0.pow((-20.0 * t + 10.0)) * sin(((20.0 * t) - 11.125) * c5)) / 2.0 + 1.0
                Ratio(v.toFloat())
            }
        }
    }

    /** 平滑步进（Smoothstep） */
    data object Smoothstep : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(t * t * (3f - 2f * t))
        }
    }
    /** 更平滑的步进（Smootherstep） */
    data object Smootherstep : Easing {
        override operator fun invoke(ratio: Ratio): Ratio {
            val t = ratio.value
            return Ratio(t * t * t * (t * (6f * t - 15f) + 10f))
        }
    }
}