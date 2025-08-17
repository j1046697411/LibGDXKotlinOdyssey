package cn.jzl.ui.modifier

interface Merge<S : Merge<S>> : Modifier.Element {

    fun mergeWith(other: S): S

    @Suppress("UNCHECKED_CAST")
    fun unsafeMergeWith(other: Modifier.Element): S = mergeWith(other as S)
}