package cn.jzl.ui.modifier

interface Modifier {
    fun <R> foldIn(initial: R, operation: (R, Element) -> R): R
    fun <R> foldOut(initial: R, operation: (R, Element) -> R): R

    fun any(predicate: (Element) -> Boolean): Boolean
    fun all(predicate: (Element) -> Boolean): Boolean

    interface Element : Modifier {
        override fun <R> foldOut(initial: R, operation: (R, Element) -> R): R = operation(initial, this)
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = operation(initial, this)
        override fun all(predicate: (Element) -> Boolean): Boolean = predicate(this)
        override fun any(predicate: (Element) -> Boolean): Boolean = predicate(this)
    }

    companion object : Modifier {
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = initial

        override fun <R> foldOut(initial: R, operation: (R, Element) -> R): R = initial

        override fun any(predicate: (Element) -> Boolean): Boolean = false

        override fun all(predicate: (Element) -> Boolean): Boolean = true
    }
}