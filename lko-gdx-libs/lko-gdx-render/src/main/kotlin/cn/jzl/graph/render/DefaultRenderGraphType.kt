package cn.jzl.graph.render

import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.validator.GraphValidationResult

class DefaultRenderGraphType(override val type: String) : RenderGraphType {
    override fun validate(graph: GraphWithProperties): GraphValidationResult {
        TODO("Not yet implemented")
    }
}