@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ui.style

import androidx.compose.runtime.Stable
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.modifier.plus
import com.badlogic.gdx.graphics.Color

@JvmInline
value class TestColor(val color: Color) : Modifier.Element

inline fun <reified E : Modifier.Element> StyleSheet.get(): E? = get(E::class)

@Stable
inline val Int.px: UIUnit get() = UIUnit.Pixel(toFloat())

@Stable
inline val Int.per: UIUnit get() = UIUnit.Percent(toFloat())

@Stable
inline val Float.px: UIUnit get() = UIUnit.Pixel(this)

@Stable
inline val Float.per: UIUnit get() = UIUnit.Percent(this)

@Stable
inline val StyleSheet.width: UIUnit get() = get<Width>()?.value ?: UIUnit.Auto

@Stable
inline val StyleSheet.height: UIUnit get() = get<Height>()?.value ?: UIUnit.Auto

@Stable
inline fun Modifier.width(value: UIUnit) = this + Width(value)

@Stable
inline fun Modifier.height(value: UIUnit) = this + Height(value)

@Stable
inline fun Modifier.size(width: UIUnit, height: UIUnit) = width(width).height(height)

@Stable
inline fun Modifier.background(color: Color) = this + TestColor(color)
