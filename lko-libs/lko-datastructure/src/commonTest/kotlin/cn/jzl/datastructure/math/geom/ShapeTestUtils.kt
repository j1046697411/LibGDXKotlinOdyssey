package cn.jzl.datastructure.math.geom

import cn.jzl.datastructure.math.vector.Point
import kotlin.math.hypot
import kotlin.math.abs
import kotlin.test.assertTrue

fun feq(expected: Float, actual: Float, eps: Float = 1e-5f) {
    val ok = abs(expected - actual) <= eps
    assertTrue(ok, "expected=$expected actual=$actual | diff=${abs(expected-actual)} <= $eps")
}

fun assertPointEquals(expected: Point, actual: Point, eps: Float = 1e-5f) {
    val d = hypot(expected.x - actual.x, expected.y - actual.y)
    assertTrue(d <= eps, "expected=$expected actual=$actual | distance=$d <= $eps")
}
