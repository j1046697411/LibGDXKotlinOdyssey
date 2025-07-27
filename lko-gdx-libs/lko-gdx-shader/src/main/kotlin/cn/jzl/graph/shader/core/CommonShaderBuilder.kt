package cn.jzl.graph.shader.core

import cn.jzl.graph.shader.core.UniformRegistry
import ktx.collections.isNotEmpty
import ktx.collections.set

abstract class CommonShaderBuilder(protected val uniformRegistry: UniformRegistry) {

    private val initialLines = ktx.collections.GdxArray<String>()
    private val mainLines = ktx.collections.GdxArray<String>()
    private val structures = ktx.collections.GdxMap<String, String>()
    private val variables = ktx.collections.GdxMap<String, Variable>()
    private val uniformVariables = ktx.collections.GdxMap<String, UniformVariable>()
    private val functions = ktx.collections.GdxMap<String, String>()


    fun addStructArrayUniformVariable(
        name: String,
        fieldNames: Array<String>,
        size: Int,
        type: String,
        global: Boolean,
        comment: String = "",
        setter: UniformRegistry.StructArrayUniformSetter
    ) {
        if (global) {
            uniformRegistry.registerGlobalStructArrayUniform(name, fieldNames, setter)
        } else {
            uniformRegistry.registerLocalStructArrayUniform(name, fieldNames, setter)
        }
        this.uniformVariables[name] = UniformVariable(name, type, size, global, comment, null)
    }

    fun addArrayUniformVariable(
        name: String,
        size: Int,
        type: String,
        global: Boolean,
        comment: String,
        setter: UniformRegistry.UniformSetter,
    ) {
        if (global) {
            uniformRegistry.registerGlobalUniform(name, setter)
        } else {
            uniformRegistry.registerLocalUniform(name, setter)
        }
        this.uniformVariables[name] = UniformVariable(name, type, size, global, comment, setter)
    }

    fun addUniformVariable(
        name: String,
        type: String,
        global: Boolean,
        comment: String,
        setter: UniformRegistry.UniformSetter
    ) {
        addArrayUniformVariable(name, -1, type, global, comment, setter)
    }

    fun addVariable(name: String, type: String, varying: Boolean = false) {
        this.variables[name] = Variable(name, type, varying)
    }

    fun addFunction(name: String, functionText: String) {
        this.functions[name] = functionText
    }

    fun addStructure(name: String, structureText: String) {
        structures[name] = structureText
    }

    fun addInitialLine(initialLine: String) {
        initialLines.add(initialLine)
    }

    fun addMainLine(mainLine: String) {
        mainLines.add(mainLine)
    }

    private fun appendUniformVariables(builder: StringBuilder) {
        if (uniformVariables.isNotEmpty()) {
            uniformVariables.forEach {
                val uniformVariable = it.value
                val name = if (uniformVariable.size > -1) "${it.key}[${uniformVariable.size}]" else it.key
                if (uniformVariable.comment.isNotEmpty()) {
                    builder.append("// ").append(uniformVariable.comment).appendLine()
                }
                builder.append("uniform ${uniformVariable.type} ${name};").appendLine()
            }
            builder.appendLine()
        }
    }

    private fun appendVariables(builder: StringBuilder) {
        if (variables.isNotEmpty()) {
            variables.forEach {
                val variable = it.value
                builder.append("${if (variable.varying) "varying" else "in"} ${variable.type} ${variable.name};")
                    .appendLine()
            }
            builder.appendLine()
        }
    }

    private fun appendFunctions(builder: StringBuilder) {
        if (functions.isNotEmpty()) {
            functions.forEach { builder.append(it.value).appendLine() }
            builder.appendLine()
        }
    }

    private fun appendStructures(builder: StringBuilder) {
        if (structures.isNotEmpty()) {
            structures.forEach {
                builder.append("struct ${it.key} {").appendLine()
                builder.append(it.value).appendLine("};")
            }
            builder.appendLine()
        }
    }

    private fun appendInitialLines(builder: StringBuilder) {
        if (initialLines.isNotEmpty()) {
            initialLines.forEach { builder.append(it).appendLine() }
            builder.appendLine()
        }
    }

    private fun appendMainLines(builder: StringBuilder) {
        if (mainLines.isNotEmpty()) {
            builder.append("void main() {").appendLine()
            mainLines.forEach { builder.append("    ").append(it).appendLine() }
            builder.appendLine("}")
        }
    }

    protected open fun appendAttributeVariables(builder: StringBuilder): Unit = Unit

    fun buildProgram(): String {
        val builder = StringBuilder()
        appendInitialLines(builder)
        appendStructures(builder)
        appendAttributeVariables(builder)
        appendUniformVariables(builder)
        appendVariables(builder)
        appendFunctions(builder)
        appendMainLines(builder)
        return builder.toString()
    }

    private data class Variable(
        val name: String,
        val type: String,
        val varying: Boolean = false,
    )

    private data class UniformVariable(
        val name: String,
        val type: String,
        val size: Int,
        val global: Boolean,
        val comment: String,
        val setter: UniformRegistry.UniformSetter?,
    )
}