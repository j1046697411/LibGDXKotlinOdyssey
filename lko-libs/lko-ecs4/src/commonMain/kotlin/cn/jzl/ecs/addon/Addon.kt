package cn.jzl.ecs.addon

import cn.jzl.di.DIMainBuilder
import cn.jzl.di.singleton
import cn.jzl.ecs.World
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.system.Phase

@ConsistentCopyVisibility
data class Namespaced @PublishedApi internal constructor(val namespace: String, val setup: WorldSetup)

fun interface Injector {
    fun inject(builder: DIMainBuilder.() -> Unit)
}

class WorldSetup(
    val injector: Injector,
    val runOnOrAfter: (String, Phase, World.() -> Unit) -> Unit,
) {

    @PublishedApi
    internal val addonInstallers = mutableMapOf<Addon<*, *>, AddonInstaller<*, *>>()

    @ECSDsl
    inline fun <reified Configuration, reified Instances> install(
        addon: Addon<Configuration, Instances>,
        noinline configuration: Configuration.() -> Unit = {}
    ) {
        val addonInstaller = addonInstallers.getOrPut(addon) {
            val addonInstaller = AddonInstaller(addon)
            injector.inject {
                val instances = addonInstaller.addon.run {
                    val config = defaultConfiguration()
                    addonInstaller.configs.forEach { config.it() }
                    onInstall(config)
                }
                if (instances != null) {
                    val bindInstances: Instances & Any = instances
                    this bind singleton(tag = addon) { bindInstances }
                }
            }
            addonInstaller
        }
        @Suppress("UNCHECKED_CAST")
        addonInstaller as AddonInstaller<Configuration, Instances>
        addonInstaller.configs.add(configuration)
    }

    @ECSDsl
    inline fun install(name: String, crossinline init: AddonSetup<Unit>.() -> Unit = {}) {
        install(createAddon(name, {}) { init() })
    }

    @ECSDsl
    inline fun namespace(namespace: String, configuration: Namespaced.() -> Unit): WorldSetup = apply {
        Namespaced(namespace, this).configuration()
    }

    data class AddonInstaller<Configuration, Instance>(
        val addon: Addon<Configuration, Instance>,
        val configs: MutableList<Configuration.() -> Unit> = mutableListOf(),
    )
}

fun <Configuration, Instance> createAddon(
    name: String,
    defaultConfiguration: WorldSetup.() -> Configuration,
    init: AddonSetup<Configuration>.() -> Instance,
): Addon<Configuration, Instance> = Addon(
    name = name,
    defaultConfiguration = defaultConfiguration,
) {
    AddonSetup(name, it, injector, runOnOrAfter).init()
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
    val injector: Injector,
    val runOnOrAfter: (String, Phase, World.() -> Unit) -> Unit
) {

    @ECSDsl
    fun injects(configuration: DIMainBuilder.() -> Unit): AddonSetup<Configuration> = apply {
        injector.inject(configuration)
    }

    @ECSDsl
    inline fun components(crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_COMPONENTS, configuration)

    @ECSDsl
    inline fun systems(crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_SYSTEMS, configuration)

    @ECSDsl
    inline fun entities(crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_ENTITIES, configuration)

    @ECSDsl
    inline fun onStart(crossinline configuration: World.() -> Unit): AddonSetup<Configuration> = on(Phase.ENABLE, configuration)

    @ECSDsl
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
