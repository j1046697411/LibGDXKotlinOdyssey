package cn.jzl.shader


interface SimpleIndenter {
    fun inline(text: CharSequence): SimpleIndenter
    fun line(text: CharSequence): SimpleIndenter
    fun indent(): SimpleIndenter
    fun unindent(): SimpleIndenter
    fun clear()
}

@JvmInline
value class Indenter(@PublishedApi internal val actions: MutableList<Action> = mutableListOf()) : SimpleIndenter {

    interface Action {
        interface Text : Action {
            val value: CharSequence
        }

        @JvmInline
        value class Inline(override val value: CharSequence) : Action, Text

        @JvmInline
        value class Line(override val value: CharSequence) : Action, Text
        data object Indent : Action
        data object Unindent : Action

        data object EmptyLine : Action
        data class Marker(val data: Any) : Action

        @JvmInline
        value class LineDeferred(val action: () -> Indenter) : Action
    }

    override fun inline(text: CharSequence): Indenter = apply {
        actions.add(Action.Inline(text))
    }

    override fun line(text: CharSequence): Indenter = apply {
        actions.add(Action.Line(text))
    }

    override fun indent(): Indenter = apply {
        actions.add(Action.Indent)
    }

    override fun unindent(): Indenter = apply {
        actions.add(Action.Unindent)
    }

    override fun clear() {
        actions.clear()
    }

    fun emptyLine(): Indenter = apply {
        actions.add(Action.EmptyLine)
    }

    fun mark(data: Any): Indenter = apply {
        actions.add(Action.Marker(data))
    }

    fun lineDeferred(action: () -> Indenter): Indenter = apply {
        actions.add(Action.LineDeferred(action))
    }
}

fun Appendable.evaluate(indenter: Indenter, indentEmptyLines: Boolean = false, handler: (Any, Int) -> Unit) {
    var line = 0
    var newLine = false
    var allowEmptyLine = false
    var indentIndex = 0
    indenter.actions.forEach { action ->
        when (action) {
            is Indenter.Action.Text -> {
                if (newLine) {
                    append("    ".repeat(indentIndex))
                    newLine = false
                }
                append(action.value)
                if (action is Indenter.Action.Line) {
                    appendLine()
                    newLine = true
                }
                allowEmptyLine = true
            }

            is Indenter.Action.EmptyLine -> {
                if (allowEmptyLine) {
                    newLine = true
                    line++
                    allowEmptyLine = false
                    appendLine()
                }
            }

            is Indenter.Action.Indent, Indenter.Action.Unindent -> {
                if (indentEmptyLines) allowEmptyLine = false
                indentIndex = if (action is Indenter.Action.Indent) indentIndex + 1 else indentIndex - 1
            }

            is Indenter.Action.Marker -> handler(action.data, indentIndex)
            is Indenter.Action.LineDeferred -> evaluate(action.action(), indentEmptyLines, handler)
        }
    }
}

fun Indenter.block(prefix: String = "", suffix: String = "", callback: Indenter.() -> Indenter): Indenter {
    return let { if (prefix.isNotEmpty()) inline(prefix) else it }
        .line("{")
        .indent()
        .let(callback)
        .unindent()
        .let { if (suffix.isNotEmpty()) inline("}").line(suffix) else it.line("}") }
}

fun Indenter.inline(prefix: String = "", suffix: String = "", callback: Indenter.() -> Indenter): Indenter {
    return let { if (prefix.isNotEmpty()) inline(prefix) else it }
        .let(callback)
        .let { if (suffix.isNotEmpty()) inline(suffix) else it }
}

fun Indenter.indent(callback: Indenter.() -> Unit): Indenter = apply {
    try {
        indent()
        callback()
    } finally {
        unindent()
    }
}