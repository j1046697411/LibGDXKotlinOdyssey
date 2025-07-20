package cn.jzl.graph.common.rendering.producer.provided

interface TimeProvider {
    val time: Float
    val deltaTime: Float
}