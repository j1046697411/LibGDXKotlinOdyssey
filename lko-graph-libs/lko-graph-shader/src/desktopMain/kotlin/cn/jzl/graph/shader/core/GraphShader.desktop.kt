package cn.jzl.graph.shader.core

import cn.jzl.shader.GlslVisitor
import cn.jzl.shader.Indenter
import cn.jzl.shader.Program
import cn.jzl.shader.evaluate
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.glutils.ShaderProgram

actual fun createGraphProgram(program: Program, uniformRegistry: DefaultUniformRegistry): GraphProgram {
    val glslVisitor = GlslVisitor()
    val indenter = Indenter()
    glslVisitor.visit(program.vertexShader, indenter)
    val vertexShader = buildString { this.evaluate(indenter) { _, _ -> } }
    indenter.clear()
    glslVisitor.visit(program.fragmentShader, indenter)
    val fragmentShader = buildString { this.evaluate(indenter) { _, _ -> } }
    val shaderProgram = ShaderProgram(vertexShader, fragmentShader)
    if (!shaderProgram.isCompiled) {
        Gdx.app.error("ShaderProgram", shaderProgram.log)
    }
    val graphProgram = GdxGraphProgram(shaderProgram)
    uniformRegistry.apply(graphProgram, graphProgram)
    return graphProgram
}

class GdxGraphProgram(private val shaderProgram: ShaderProgram) : GraphProgram, ShaderLocationBinder, GraphProgramRegistry {

    private val attributes = mutableMapOf<String, Attribute>()
    private val uniforms = mutableMapOf<String, Uniform>()

    data class Uniform(val alias: String, val global: Boolean, val location: Int, val setter: UniformSetter)
    data class Attribute(val alias: String, val componentCount: Int, val location: Int)

    private val globalUniforms: Sequence<Uniform> = uniforms.values.asSequence().filter { uniform -> uniform.global }
    private val localUniforms: Sequence<Uniform> = uniforms.values.asSequence().filter { uniform -> !uniform.global }

    override fun begin(context: ShaderContext) {
        shaderProgram.bind()
        globalUniforms.forEach { it.setter.run { context.set(it.location) } }
    }

    override fun render(context: ShaderContext) {
        localUniforms.forEach { it.setter.run { context.set(it.location) } }
        TODO("Not yet implemented")
    }

    override fun end() {
        TODO("Not yet implemented")
    }

    override fun registerAttribute(alias: String, componentCount: Int, location: Int) {
        attributes[alias] = Attribute(alias, componentCount, location)
    }

    override fun registerUniform(alias: String, global: Boolean, location: Int, setter: UniformSetter) {
        uniforms[alias] = Uniform(alias, global, location, setter)
    }

    override fun getAttributeLocation(alias: String): Int {
        return shaderProgram.getAttributeLocation(alias)
    }

    override fun getUniformLocation(alias: String, pedantic: Boolean): Int {
        return shaderProgram.fetchUniformLocation(alias, pedantic)
    }

    override fun setUniform(location: Int, v: Int) {
        shaderProgram.setUniformi(location, v)
    }

    override fun setUniform(location: Int, v1: Int, v2: Int) {
        shaderProgram.setUniformi(location, v1, v2)
    }

    override fun setUniform(location: Int, v1: Int, v2: Int, v3: Int) {
        shaderProgram.setUniformi(location, v1, v2, v3)
    }

    override fun setUniform(location: Int, v1: Int, v2: Int, v3: Int, v4: Int) {
        shaderProgram.setUniformi(location, v1, v2, v3, v4)
    }

    override fun setUniform(location: Int, v1: Float) {
        shaderProgram.setUniformf(location, v1)
    }

    override fun setUniform(location: Int, v1: Float, v2: Float) {
        shaderProgram.setUniformf(location, v1, v2)
    }

    override fun setUniform(location: Int, v1: Float, v2: Float, v3: Float) {
        shaderProgram.setUniformf(location, v1, v2, v3)
    }

    override fun setUniform(location: Int, v1: Float, v2: Float, v3: Float, v4: Float) {
        shaderProgram.setUniformf(location, v1, v2, v3, v4)
    }

    override fun setUniform1(location: Int, value: FloatArray, offset: Int) {
        shaderProgram.setUniform1fv(location, value, offset, 1)
    }

    override fun setUniform2(location: Int, value: FloatArray, offset: Int) {
        shaderProgram.setUniform2fv(location, value, offset, 2)
    }

    override fun setUniform3(location: Int, value: FloatArray, offset: Int) {
        shaderProgram.setUniform3fv(location, value, offset, 3)
    }

    override fun setUniform4(location: Int, value: FloatArray, offset: Int) {
        shaderProgram.setUniform4fv(location, value, offset, 4)
    }

    override fun setUniformMatrix4(location: Int, value: FloatArray, offset: Int) {
        shaderProgram.setUniformMatrix4fv(location, value, offset, 16)
    }
}
