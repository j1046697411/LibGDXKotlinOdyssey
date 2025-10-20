package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point

/**
 * 二维简单形状的统一接口。
 *
 * 约定与语义：
 * - `closed` 表示几何是否闭合（如三角形/矩形/圆为 true，线段/折线为 false）。
 * - `area`/`perimeter` 分别为面积与周长（开放形状的面积为 0）。
 * - `center` 为形状的代表中心（如几何中心或质心）。
 * - `distance(point)` 返回点到形状的最短距离（内部点返回 0）。
 * - `projectedPoint(point)` 返回点在形状边界上的最近投影（内部点返回自身）。
 * - `normalVectorAt(point)` 返回指向该点的单位外法线；投影点处定义为外法线方向。
 * - `contains(point)` 判断点是否在形状内或边界上。
 * - `getBounds()` 返回包含该形状的最小轴对齐矩形。
 */
interface SimpleShape2D {
    val closed: Boolean
    val area: Float
    val perimeter: Float
    val center: Point

    fun distance(point: Point): Float

    fun normalVectorAt(point: Point): Point

    fun projectedPoint(point: Point): Point

    operator fun contains(point: Point): Boolean

    fun getBounds() : Rectangle
}

