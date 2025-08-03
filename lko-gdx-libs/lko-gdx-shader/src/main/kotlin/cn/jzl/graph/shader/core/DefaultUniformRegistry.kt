package cn.jzl.graph.shader.core

class DefaultUniformRegistry : UniformRegistry {

    private val registryCallbacks = mutableListOf<RegistryCallback>()

    private fun callback(registryCallback: RegistryCallback) {
        registryCallbacks.add(registryCallback)
    }

    override fun registerAttribute(alias: String, componentCount: Int) = callback {
        registerAttribute(alias, componentCount, it.getAttributeLocation(alias))
    }

    override fun registerGlobalUniform(alias: String, setter: UniformRegistry.UniformSetter) = callback {
        registerUniform(alias, true, it.getUniformLocation(alias), setter)
    }

    override fun registerLocalUniform(alias: String, setter: UniformRegistry.UniformSetter) = callback {
        registerUniform(alias, false, it.getUniformLocation(alias), setter)
    }

    override fun registerGlobalStructArrayUniform(alias: String, fieldNames: Array<String>, setter: UniformRegistry.StructArrayUniformSetter) = callback {
        registerStructArrayUniform(alias, true, fieldNames, setter)
    }

    override fun registerLocalStructArrayUniform(alias: String, fieldNames: Array<String>, setter: UniformRegistry.StructArrayUniformSetter) {
        registerStructArrayUniform(alias, false, fieldNames, setter)
    }

    private fun registerStructArrayUniform(alias: String, global: Boolean, fieldNames: Array<String>, setter: UniformRegistry.StructArrayUniformSetter) = callback {
        val startIndex = it.getUniformLocation("$alias[0].${fieldNames.component1()}")
        val size = it.getUniformLocation("$alias[1].${fieldNames.component1()}") - startIndex
        val offsets = IntArray(fieldNames.size)
        for (i in fieldNames.indices) {
            offsets[i] = it.getUniformLocation("$alias[0].${fieldNames[i]}") - startIndex
        }
        registerStructArrayUniform(alias, global, fieldNames, startIndex, size, offsets, setter)
    }

    override fun apply(shader: Shader, binder: ShaderLocationBinder) {
        registryCallbacks.forEach { it.run { shader.register(binder) } }
        registryCallbacks.clear()
    }

    private fun interface RegistryCallback {
        fun Shader.register(shaderLocationBinder: ShaderLocationBinder)
    }
}