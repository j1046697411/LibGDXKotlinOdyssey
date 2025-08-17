package cn.jzl.ui.modifier

interface Modifier {
    fun <R> foldIn(initial: R, operation: (R, Element) -> R): R
    fun <R> foldOut(initial: R, operation: (R, Element) -> R): R

    fun any(operation: (Element) -> Boolean): Boolean
    fun all(operation: (Element) -> Boolean): Boolean

    interface Element : Modifier {
        override fun <R> foldOut(initial: R, operation: (R, Element) -> R): R = operation(initial, this)
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = operation(initial, this)
        override fun all(operation: (Element) -> Boolean): Boolean = operation(this)
        override fun any(operation: (Element) -> Boolean): Boolean = operation(this)
    }

    companion object : Modifier {
        override fun <R> foldIn(initial: R, operation: (R, Element) -> R): R = initial

        override fun <R> foldOut(initial: R, operation: (R, Element) -> R): R = initial

        override fun any(operation: (Element) -> Boolean): Boolean = false

        override fun all(operation: (Element) -> Boolean): Boolean = true
    }
}