package cn.jzl.graph.shader.core

class DefaultUniformRegistry : UniformRegistry {

    private val registryCallbacks = mutableListOf<UniformRegistry.RegistryCallback>()

    override fun register(registryCallback: UniformRegistry.RegistryCallback): UniformRegistry = also {
        registryCallbacks.add(registryCallback)
    }

    fun apply(shader: GraphProgramRegistry, binder: ShaderLocationBinder) {
        registryCallbacks.forEach { it.run { shader.register(binder) } }
        registryCallbacks.clear()
    }
}