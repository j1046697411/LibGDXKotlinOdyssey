package cn.jzl.sect.ecs

import cn.jzl.ecs.World
import cn.jzl.ecs.addon.createAddon
import cn.jzl.ecs.componentId
import cn.jzl.ecs.observers.emit
import cn.jzl.ecs.query.EntityQueryContext
import cn.jzl.ecs.relations
import cn.jzl.ecs.system.system
import kotlin.time.Duration

@JvmInline
value class Countdown(val value: Duration)
sealed class OnCountdownComplete

val countdownAddon = createAddon("countdown") {
    components {
        world.componentId<Countdown>()
        world.componentId<OnCountdownComplete>()
    }

    systems {
        system(CountdownContext(world), "countdownSystem").exec(CountdownContext::update)
    }
}

class CountdownContext(world: World) : EntityQueryContext(world) {
    var countdown by component<Countdown>()
}

fun CountdownContext.update(delta: Duration) {
    if (countdown.value <= delta) {
        removeRelation(relations.component<Countdown>())
        world.emit<OnCountdownComplete>(entity)
        return
    }
    countdown = Countdown(countdown.value - delta)
}
