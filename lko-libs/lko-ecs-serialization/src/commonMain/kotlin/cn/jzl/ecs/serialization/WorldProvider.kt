package cn.jzl.ecs.serialization

import cn.jzl.ecs.World
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.overwriteWith
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

class WorldProvider(val world: World) : DeserializationStrategy<World> {

    @OptIn(ExperimentalSerializationApi::class)
    override val descriptor: SerialDescriptor = ContextualSerializer(Any::class).descriptor

    override fun deserialize(decoder: Decoder): World = world
}

@Suppress("CAST_NEVER_SUCCEEDS")
@OptIn(ExperimentalSerializationApi::class)
val SerializersModule.world: World get() = (getContextual(World::class) as WorldProvider).world

data class ComponentSerializersBuilder(
    val modules: MutableList<SerializersModule> = mutableListOf(),
    val serialNameToClass: MutableMap<String, KClass<out Any>> = mutableMapOf()
) {
    fun build(): ComponentSerializers = SerializersByMap(
        modules.fold(EmptySerializersModule()) { acc, module -> acc.overwriteWith(module) },
        serialNameToClass.toMap()
    )
}

private class SerializersByMap(
    override val module: SerializersModule,
    private val serialName2Component: Map<String, KClass<out Any>>
) : ComponentSerializers {

    private val component2serialName: Map<KClass<out Any>, String> = serialName2Component.entries.associate { (k, v) -> v to k }

    override fun getClassFor(serialName: String, namespaces: List<String>): KClass<out Any> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getSerializerFor(key: String, baseClass: KClass<in T>): DeserializationStrategy<T> {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getSerializerFor(kClass: KClass<in T>): DeserializationStrategy<T> {
        TODO("Not yet implemented")
    }

    override fun getSerialNameFor(kClass: KClass<out Any>): String {
        TODO("Not yet implemented")
    }

    override fun <T : Any> getKClassFor(serializer: KSerializer<T>): KClass<T>? {
        TODO("Not yet implemented")
    }
}

class SerializableComponentsBuilder(
    private val world: World,
    @PublishedApi
    internal val serializers: ComponentSerializersBuilder = ComponentSerializersBuilder(),
) {

    inline fun module(configure: SerializersModuleBuilder.() -> Unit) {
        serializers.modules += SerializersModule { configure() }
    }

    inline fun components(crossinline configure: PolymorphicModuleBuilder<Any>.() -> Unit) = module {
        polymorphic(Any::class) { configure() }
    }

    inline fun <reified C : Any> PolymorphicModuleBuilder<C>.component() = component(C::class)

    inline fun <reified C : Any> PolymorphicModuleBuilder<C>.component(serializer: KSerializer<C>) = component(C::class, serializer)

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    fun <C : Any> PolymorphicModuleBuilder<C>.component(subclass: KClass<C>, serializer: KSerializer<C> = subclass.serializer()) {
        val serialName = serializer.descriptor.serialName
        serializers.serialNameToClass[serialName] = subclass
        subclass(subclass, serializer)
    }
}