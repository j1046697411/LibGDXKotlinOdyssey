package cn.jzl.graph.common.time

import kotlin.time.Duration
import kotlin.time.DurationUnit

class DefaultTimeKeeper : TimeKeeper {
    var timeMultiplier: Float = 1.0f
    var paused: Boolean = false

    override var time: Duration = Duration.ZERO
        private set
    override var deltaTime: Duration = Duration.ZERO
        private set

    override fun update(deltaTime: Duration) {
        if (paused) {
            this.deltaTime = Duration.ZERO
            return
        }
        this.deltaTime = deltaTime * timeMultiplier.toDouble()
        this.time += this.deltaTime
    }
}