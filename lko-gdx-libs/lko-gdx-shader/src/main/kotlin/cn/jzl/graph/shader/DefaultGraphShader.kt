package cn.jzl.graph.shader

import cn.jzl.graph.render.OpenGLContext
import cn.jzl.graph.shader.core.*
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4

class DefaultGraphShader(
    private val openGLContext: OpenGLContext,
    private val uniformRegistry: UniformRegistry,
    val shaderProgram: ShaderProgram,
    override val tag: String
) : GraphShader {

    override val attributes: MutableMap<String, GraphShader.Attribute> = mutableMapOf()
    private val uniforms: MutableMap<String, GraphShader.Uniform> = mutableMapOf()
    private val structArrayUniforms: MutableMap<String, GraphShader.StructArrayUniform> = mutableMapOf()

    fun initialize() {
        uniformRegistry.apply(this, object : ShaderLocationBinder {
            override fun getAttributeLocation(alias: String): Int {
                return shaderProgram.getAttributeLocation(alias)
            }

            override fun getUniformLocation(alias: String, pedantic: Boolean): Int {
                return shaderProgram.fetchUniformLocation(alias, pedantic)
            }
        })
    }

    override fun begin(shaderContext: ShaderContext, openGLContext: OpenGLContext) {
        shaderProgram.bind()
        for (uniform in uniforms.values) {
            if (uniform.global && uniform.location != -1) {
                uniform.uniformSetter.set(shaderContext, this, uniform.location)
            }
        }
        for (struct in structArrayUniforms.values) {
            if (struct.global && struct.startIndex != -1) {
                struct.setter.set(shaderContext, this, struct.startIndex, struct.fieldOffsets, struct.size)
            }
        }
    }

    override fun end() {
        shaderProgram.end()
    }

    override fun render(shaderContext: ShaderContext, configuration: ShaderRendererConfiguration<Any>) {
        for (uniform in uniforms.values) {
            if (!uniform.global && uniform.location != -1) {
                uniform.uniformSetter.set(shaderContext, this, uniform.location)
            }
        }
        for (struct in structArrayUniforms.values) {
            if (!struct.global && struct.startIndex != -1) {
                struct.setter.set(shaderContext, this, struct.startIndex, struct.fieldOffsets, struct.size)
            }
        }
        configuration.render(shaderContext, shaderProgram, shaderContext.model) { attributes.getValue(it).location }
    }

    override fun setUniform(location: Int, value: Matrix4) {
        shaderProgram.setUniformMatrix(location, value)
    }

    override fun setUniform(location: Int, value: Matrix3) {
        shaderProgram.setUniformMatrix(location, value)
    }

    override fun setUniform(location: Int, value: Vector4) {
        shaderProgram.setUniformf(location, value)
    }

    override fun setUniform(location: Int, value: Vector3) {
        shaderProgram.setUniformf(location, value)
    }

    override fun setUniform(location: Int, value: Vector2) {
        shaderProgram.setUniformf(location, value)
    }

    override fun setUniform(location: Int, value: Color) {
        shaderProgram.setUniformf(location, value)
    }

    override fun setUniform(location: Int, value: Float) {
        shaderProgram.setUniformf(location, value)
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

    override fun setUniform(location: Int, value: Int) {
        shaderProgram.setUniformi(location, value)
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

    override fun setUniform(location: Int, textureDesc: TextureDescriptor<*>) {
        shaderProgram.setUniformi(location, openGLContext.bindTexture(textureDesc))
    }

    override fun setUniform(location: Int, texture: GLTexture) {
        shaderProgram.setUniformi(location, openGLContext.bindTexture(texture))
    }

    override fun setUniformMatrix4Array(location: Int, values: FloatArray) {
        shaderProgram.setUniformMatrix4fv(location, values, 0, values.size)
    }

    override fun setUniformFloatArray(location: Int, values: FloatArray) {
        shaderProgram.setUniform1fv(location, values, 0, values.size)
    }

    override fun setUniformVector2Array(location: Int, values: FloatArray) {
        shaderProgram.setUniform2fv(location, values, 0, values.size)
    }

    override fun setUniformVector3Array(location: Int, values: FloatArray) {
        shaderProgram.setUniform3fv(location, values, 0, values.size)
    }

    override fun registerAttribute(alias: String, componentCount: Int, location: Int) {
        attributes[alias] = GraphShader.Attribute(alias, componentCount, location)
    }

    override fun registerUniform(alias: String, global: Boolean, location: Int, uniformSetter: UniformRegistry.UniformSetter) {
        uniforms[alias] = GraphShader.Uniform(alias, global, location, uniformSetter)
    }

    override fun registerStructArrayUniform(
        alias: String,
        global: Boolean,
        fieldNames: Array<String>,
        startIndex: Int,
        size: Int,
        fieldOffsets: IntArray,
        setter: UniformRegistry.StructArrayUniformSetter
    ) {
        structArrayUniforms[alias] = GraphShader.StructArrayUniform(alias, global, fieldNames, startIndex, size, fieldOffsets, setter)
    }

}