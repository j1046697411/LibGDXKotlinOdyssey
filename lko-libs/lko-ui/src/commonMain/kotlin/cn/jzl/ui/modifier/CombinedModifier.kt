package cn.jzl.ui.modifier

@PublishedApi
internal data class CombinedModifier(private val outer: Modifier, private val inner: Modifier) : Modifier {
    override fun <R> foldIn(initial: R, operation: (R, Modifier.Element) -> R): R {
        return inner.foldIn(outer.foldIn(initial, operation), operation)
    }

    override fun <R> foldOut(initial: R, operation: (R, Modifier.Element) -> R): R {
        return outer.foldOut(inner.foldOut(initial, operation), operation)
    }

    override fun any(predicate: (Modifier.Element) -> Boolean): Boolean {
        return outer.any(predicate) || inner.any(predicate)
    }

    override fun all(predicate: (Modifier.Element) -> Boolean): Boolean {
        return outer.all(predicate) && inner.all(predicate)
    }
}