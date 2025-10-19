package cn.jzl.datastructure.math

import kotlin.math.*

/**
 * 插值器接口：定义从 `start` 到 `end` 按给定 `ratio` 进行插值的策略。
 *
 * - 典型用法：`ratio.interpolate(start, end, interpolator)`
 * - 与 [Interpolable] 配合：当类型本身可插值时，优先使用其内建插值实现
 */
fun interface Interpolator<T> {
    /**
     * 按比例 `ratio` 在 `start` 与 `end` 之间插值。
     * `ratio` 通常在 `[0f, 1f]`，但本库不强制约束。
     */
    fun interpolate(start: T, end: T, ratio: Ratio): T
}

/**
 * 可插值接口：类型自身提供到某个终点的插值实现。
 *
 * - 典型用法：`ratio.interpolate(start, end)`（当 `T : Interpolable<T>` 时）
 */
fun interface Interpolable<T> {
    /**
     * 在当前对象与 `end` 之间按 `ratio` 插值，返回新的对象实例。
     */
    fun interpolateTo(end: T, ratio: Ratio): T
}

/**
 * 对当前比例应用缓动函数，返回变换后的比例。
 */
fun Ratio.ease(easing: Easing): Ratio = easing(this)

/**
 * 使用显式的 [Interpolator] 对任意类型 `T` 进行插值。
 */
fun <T> Ratio.interpolate(start: T, end: T, interpolator: Interpolator<T>): T = interpolator.interpolate(start, end, this)

/**
 * 当类型实现了 [Interpolable] 时，使用其内建插值实现。
 */
fun <T : Interpolable<T>> Ratio.interpolate(start: T, end: T): T = start.interpolateTo(end, this)

/**
 * 对 `Float` 进行线性插值：`(end - start) * value + start`。
 */
fun Ratio.interpolate(start: Float, end: Float): Float = start.lerp(end, this)

/**
 * 对 `Double` 进行线性插值：`(end - start) * value + start`。
 */
fun Ratio.interpolate(start: Double, end: Double): Double = start.lerp(end, this)

/**
 * 对 `Int` 进行线性插值，结果向下取整：`((end - start) * value).toInt() + start`。
 */
fun Ratio.interpolate(start: Int, end: Int): Int = start.lerp(end, this)

/**
 * 对 `Long` 进行线性插值，结果向下取整：`((end - start) * value).toLong() + start`。
 */
fun Ratio.interpolate(start: Long, end: Long): Long = start.lerp(end, this)

/**
 * 将比例约束到指定区间（默认约束到 0..1）。
 */
fun Ratio.clamp(min: Float = 0f, max: Float = 1f): Ratio = Ratio(value.coerceIn(min, max))

/**
 * 反转比例（例如 0.2 -> 0.8）。
 */
fun Ratio.reverse(): Ratio = Ratio(1f - value)

/**
 * 镜像比例（YoYo）：在 0..1 内形成先升后降的对称曲线。
 */
fun Ratio.yoyo(): Ratio = if (value <= 0.5f) Ratio(value * 2f) else Ratio((1f - value) * 2f)

/**
 * PingPong：对比例进行往返反射，便于循环动画（单次映射）。
 */
fun Ratio.pingPong(): Ratio = Ratio(abs(((value + 1f) % 2f) - 1f))