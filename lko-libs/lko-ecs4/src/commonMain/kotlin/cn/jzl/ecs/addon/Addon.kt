package cn.jzl.ecs.addon

import cn.jzl.di.DIMainBuilder
import cn.jzl.di.singleton
import cn.jzl.ecs.World
import cn.jzl.ecs.WorldOwner
import cn.jzl.ecs.query.ECSDsl
import cn.jzl.ecs.system.Phase

@ConsistentCopyVisibility
data class Namespaced @PublishedApi internal constructor(val namespace: String, val setup: WorldSetup)

fun interface Injector {
    fun inject(builder: DIMainBuilder.() -> Unit)
}

class WorldSetup(
    val injector: Injector,
    val phaseTaskRegistry: (String, Phase, WorldOwner.() -> Unit) -> Unit,
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
                if (instances != null && instances != Unit) {
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
    AddonSetup(name, it, this).init()
}

fun <Instance> createAddon(
    name: String,
    init: AddonSetup<Unit>.() -> Instance
): Addon<Unit, Instance> = createAddon(name, { }, init)

data class AddonSetup<Configuration>(
    val name: String,
    val configuration: Configuration,
    @PublishedApi internal val worldSetup: WorldSetup
) {

    inline fun <reified Configuration1,reified Instance> install(
        addon: Addon<Configuration1, Instance>,
        noinline configuration: Configuration1.() -> Unit = {}) {
        worldSetup.install(addon, configuration)
    }

    @ECSDsl
    fun injects(configuration: DIMainBuilder.() -> Unit): AddonSetup<Configuration> = apply {
        worldSetup.injector.inject(configuration)
    }

    @ECSDsl
    inline fun components(crossinline configuration: WorldOwner.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_COMPONENTS, configuration)

    @ECSDsl
    inline fun systems(crossinline configuration: WorldOwner.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_SYSTEMS, configuration)

    @ECSDsl
    inline fun entities(crossinline configuration: WorldOwner.() -> Unit): AddonSetup<Configuration> = on(Phase.INIT_ENTITIES, configuration)

    @ECSDsl
    inline fun onStart(crossinline configuration: WorldOwner.() -> Unit): AddonSetup<Configuration> = on(Phase.ENABLE, configuration)

    @ECSDsl
    inline fun on(phase: Phase, crossinline configuration: WorldOwner.() -> Unit): AddonSetup<Configuration> = apply {
        worldSetup.phaseTaskRegistry(name, phase) { configuration() }
    }
}

data class Addon<Configuration, Instance>(
    val name: String,
    val defaultConfiguration: WorldSetup.() -> Configuration,
    val onInstall: WorldSetup.(Configuration) -> Instance,
)
