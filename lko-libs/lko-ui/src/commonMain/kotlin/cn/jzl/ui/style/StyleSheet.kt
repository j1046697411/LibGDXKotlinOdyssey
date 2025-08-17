package cn.jzl.ui.style

import cn.jzl.ui.modifier.Merge
import cn.jzl.ui.modifier.Modifier
import kotlin.reflect.KClass

interface StyleSheet : Modifier {

    operator fun <E : Modifier.Element> get(type: KClass<E>): E?

    companion object {
        operator fun invoke(modifier: Modifier): StyleSheet = object : StyleSheet, Modifier by modifier {

            private val styles = modifier.foldIn(mutableMapOf<KClass<*>, Modifier.Element>()) { acc, element ->
                val type = element::class
                val other = acc[type]
                val merged = if (other != null && element is Merge<*>) {
                    element.unsafeMergeWith(other)
                } else {
                    element
                }
                acc[type] = merged
                acc
            }

            @Suppress("UNCHECKED_CAST")
            override fun <E : Modifier.Element> get(type: KClass<E>): E? {
                return styles[type] as? E  // 使用安全转换
            }
        }
    }
}