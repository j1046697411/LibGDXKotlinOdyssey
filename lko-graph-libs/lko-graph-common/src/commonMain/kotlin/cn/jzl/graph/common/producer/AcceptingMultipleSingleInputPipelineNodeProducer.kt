package cn.jzl.graph.common.producer

import cn.jzl.graph.common.GraphType
import cn.jzl.graph.common.PipelineNode
import cn.jzl.graph.impl.NamedGraphNodeInput

abstract class AcceptingMultipleSingleInputPipelineNodeProducer<PN : PipelineNode, GT : GraphType<in PN>>(
    name: String,
    type: String,
    menuLocation: String,
) : SingleOutputPipelineNodeProducer<PN, GT>(name, type, menuLocation) {
    protected abstract val inputs: NamedGraphNodeInput
}