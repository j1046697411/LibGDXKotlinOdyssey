package cn.jzl.graph.common.rendering

interface PreparedPipeline {
    fun beforeFrame() : Unit = Unit
    fun afterFrame() : Unit = Unit
}