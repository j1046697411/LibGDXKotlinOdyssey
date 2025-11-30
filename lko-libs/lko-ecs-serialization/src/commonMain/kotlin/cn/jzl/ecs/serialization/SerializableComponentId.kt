package cn.jzl.ecs.serialization

import cn.jzl.ecs.ComponentId
import cn.jzl.ecs.World
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.reflect.KClass

typealias SerializableComponentId = @Contextual ComponentId

interface ComponentSerializers {
    val module: SerializersModule

    fun getClassFor(serialName: String, namespaces: List<String> = mutableListOf()): KClass<out Any>

    fun <T : Any> getSerializerFor(key: String, baseClass: KClass<in T>): DeserializationStrategy<T>

    fun <T : Any> getSerializerFor(kClass: KClass<in T>): DeserializationStrategy<T>

    fun getSerialNameFor(kClass: KClass<out Any>): String

    fun <T : Any> getKClassFor(serializer: KSerializer<T>): KClass<T>?
}

class ComponentIdSerializer(
    private val world: World,
    private val componentSerializers: ComponentSerializers
) : KSerializer<SerializableComponentId> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EventComponent", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: SerializableComponentId) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): SerializableComponentId {
        TODO("Not yet implemented")
    }

}