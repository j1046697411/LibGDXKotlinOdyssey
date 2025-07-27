package cn.jzl.graph.common.rendering

import cn.jzl.ecs.World

fun interface PipelinePlugin {
    fun setup(world: World, pipelineRegistry: PipelineRegistry)
}

