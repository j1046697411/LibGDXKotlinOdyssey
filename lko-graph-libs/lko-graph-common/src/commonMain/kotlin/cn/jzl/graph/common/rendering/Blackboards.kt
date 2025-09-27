package cn.jzl.graph.common.rendering

import cn.jzl.graph.GraphNode
import cn.jzl.graph.common.PipelineNodeInput
import cn.jzl.graph.common.PipelineNodeOutput

operator fun PipelineBlackboard.get(input: PipelineNodeInput): Any? {
    return this[input.fromGraphNode, input.fromOutput, input.outputType]
}

operator fun PipelineBlackboard.set(graphNode: GraphNode, output: PipelineNodeOutput, value: Any) {
    this[graphNode, output.output, output.outputType] = value
}
