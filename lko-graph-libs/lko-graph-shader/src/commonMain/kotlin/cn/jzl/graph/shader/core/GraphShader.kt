package cn.jzl.graph.shader.core

import cn.jzl.shader.*

fun buildGraphProgram(block: GraphProgramScope.() -> Unit): GraphProgram {
    val uniformRegistry = DefaultUniformRegistry()
    val program = SimpleProgram()
    val graphProgramScope = DefaultGraphProgramScope(uniformRegistry, program)
    graphProgramScope.block()
    return createGraphProgram(program, uniformRegistry)
}

expect fun createGraphProgram(program: Program, uniformRegistry: DefaultUniformRegistry): GraphProgram