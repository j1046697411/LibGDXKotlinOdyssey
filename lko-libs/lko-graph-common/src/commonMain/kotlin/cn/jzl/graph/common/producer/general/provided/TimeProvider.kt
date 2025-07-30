package cn.jzl.graph.common.producer.general.provided

interface TimeProvider {
    val time: Float
    val deltaTime: Float
}