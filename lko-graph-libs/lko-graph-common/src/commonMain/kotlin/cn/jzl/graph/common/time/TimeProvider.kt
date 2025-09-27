package cn.jzl.graph.common.time

import kotlin.time.Duration

interface TimeProvider {
    val time: Duration
    val deltaTime: Duration
}


