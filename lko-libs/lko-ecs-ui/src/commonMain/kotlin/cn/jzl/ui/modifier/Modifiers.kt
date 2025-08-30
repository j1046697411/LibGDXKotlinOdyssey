@file:Suppress("NOTHING_TO_INLINE")

package cn.jzl.ui.modifier

import androidx.compose.runtime.Stable

@Stable
inline operator fun Modifier.plus(modifier: Modifier): Modifier = when {
    this == Modifier -> modifier
    modifier == Modifier -> this
    else -> CombinedModifier(this, modifier)
}

