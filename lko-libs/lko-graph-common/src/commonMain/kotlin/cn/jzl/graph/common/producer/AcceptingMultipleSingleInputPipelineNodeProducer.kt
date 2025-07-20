package cn.jzl.graph.common.producer

import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.impl.NamedGraphNodeInput

abstract class AcceptingMultipleSingleInputPipelineNodeProducer<PN : PipelineNode>(
    name: String,
    type: String
) : SingleOutputPipelineNodeProducer<PN>(name, type) {
    protected abstract val inputs: NamedGraphNodeInput
}