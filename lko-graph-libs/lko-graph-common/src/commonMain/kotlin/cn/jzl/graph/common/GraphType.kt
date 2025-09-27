package cn.jzl.graph.common

import cn.jzl.graph.common.data.GraphWithProperties
import cn.jzl.graph.validator.GraphValidationResult

interface GraphType<PN : PipelineNode> {

    val type: String

    fun validate(graph: GraphWithProperties): GraphValidationResult
}

