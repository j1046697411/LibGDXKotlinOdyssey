package cn.jzl.ecs.addon

import cn.jzl.di.DIMainBuilder
import cn.jzl.ecs.World
import cn.jzl.ecs.system.Phase

@ConsistentCopyVisibility
data class Namespaced @PublishedApi internal constructor(val namespace: String, val setup: WorldSetup)

class WorldSetup(
    val configurator: (DIMainBuilder.() -> Unit) -> Unit,
    val runOnOrAfter: (String, Phase, World.() -> Unit) -> Unit,
) {

    inline fun <reified Configuration, reified Instances> install(
        addon: Addon<Configuration, Instances>,
        crossinline configuration: Configuration.() -> Unit = {}
    ): Configuration = addon.run {
        val configuration = defaultConfiguration()
        configuration.configuration()
        val instance = onInstall(configuration)
        runOnOrAfter(name, Phase.ADDONS_CONFIGURED) { addonService.install(addon, instance) }
        configuration
    }

    inline fun install(name: String, crossinline init: AddonSetup<Unit>.() -> Unit = {}) {
        install(createAddon(name, {}) { init() })
    }

    inline fun namespace(namespace: String, configuration: Namespaced.() -> Unit): WorldSetup = apply {
        Namespaced(namespace, this).configuration()
    }
}

fun <Configuration, Instance> createAddon(
    name: String,
    defaultConfiguration: WorldSetup.() -> Configuration,
    init: AddonSetup<Configuration>.() -> Instance,
): Addon<Configuration, Instance> = Addon(
    name = name,
    defaultConfiguration = defaultConfiguration,
) {
    AddonSetup(name, it, configurator, runOnOrAfter).init()
}

class AddonService(world: World) {

    @PublishedApi
    internal val addonToInstances = mutableMapOf<String, AddonToInstance<*>>()

    inline fun <reified Instance> install(addon: Addon<*, Instance>, instance: Instance) {
        println("install ${addon.name} with instance $instance")
        addonToInstances[addon.name] = AddonToInstance(addon, instance)
    }

    @PublishedApi
    internal data class AddonToInstance<Instance>(val addon: Addon<*, Instance>, val instance: Instance)
}

data class AddonSetup<Configuration>(
    val name: String,
    val configuration: Configuration,
    val configurator: (DIMainBuilder.() -> Unit) -> Unit,
    val runOnOrAfter: (String, Phase, World.() -> Unit) -> Unit
) {
    fun injects(configuration: DIMainBuilder.() -> Unit): AddonSetup<Configuration> = apply {
        configurator(configuration)
    }

    inline fun components(crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_COMPONENTS, configuration)

    inline fun systems(crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_SYSTEMS, configuration)

    inline fun entities(crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_ENTITIES, configuration)

    inline fun onStart(crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = on(Phase.ENABLE, configuration)

    inline fun on(phase: Phase, crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = apply {
        runOnOrAfter(name, phase) { configuration() }
    }
}

data class Addon<Configuration, Instance>(
    val name: String,
    val defaultConfiguration: WorldSetup.() -> Configuration,
    val onInstall: WorldSetup.(Configuration) -> Instance,
) {
    fun withConfig(
        customConfiguration: WorldSetup.() -> Configuration
    ): Addon<Configuration, Instance> = copy(defaultConfiguration = customConfiguration)
}
