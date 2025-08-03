package cn.jzl.graph.shader.core

import cn.jzl.graph.render.OpenGLContext
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Vector4

interface GraphShader : Shader {
    val tag: String

    val attributes: Map<String, Attribute>

    fun begin(shaderContext: ShaderContext, openGLContext: OpenGLContext)

    fun end()

    fun render(shaderContext: ShaderContext, configuration: ShaderRendererConfiguration<Any>)

    fun setUniform(location: Int, value: Matrix4)

    fun setUniform(location: Int, value: Matrix3)

    fun setUniform(location: Int, value: Vector4)

    fun setUniform(location: Int, value: Vector3)

    fun setUniform(location: Int, value: Vector2)

    fun setUniform(location: Int, value: Color)

    fun setUniform(location: Int, value: Float)

    fun setUniform(location: Int, v1: Float, v2: Float)

    fun setUniform(location: Int, v1: Float, v2: Float, v3: Float)

    fun setUniform(location: Int, v1: Float, v2: Float, v3: Float, v4: Float)

    fun setUniform(location: Int, value: Int)

    fun setUniform(location: Int, v1: Int, v2: Int)

    fun setUniform(location: Int, v1: Int, v2: Int, v3: Int)

    fun setUniform(location: Int, v1: Int, v2: Int, v3: Int, v4: Int)

    fun setUniform(location: Int, textureDesc: TextureDescriptor<*>)

    fun setUniform(location: Int, texture: GLTexture)

    fun setUniformMatrix4Array(location: Int, values: FloatArray)

    fun setUniformFloatArray(location: Int, values: FloatArray)

    fun setUniformVector2Array(location: Int, values: FloatArray)

    fun setUniformVector3Array(location: Int, values: FloatArray)

    data class Attribute(val alias: String, val componentCount: Int, val location: Int)
    data class Uniform(val alias: String, val global: Boolean, val location: Int, val uniformSetter: UniformRegistry.UniformSetter)
    data class StructArrayUniform(
        val alias: String,
        val global: Boolean,
        val fieldNames: Array<String>,
        val startIndex: Int,
        val size: Int,
        val fieldOffsets: IntArray,
        val setter: UniformRegistry.StructArrayUniformSetter
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as StructArrayUniform

            if (startIndex != other.startIndex) return false
            if (size != other.size) return false
            if (alias != other.alias) return false
            if (!fieldNames.contentEquals(other.fieldNames)) return false
            if (!fieldOffsets.contentEquals(other.fieldOffsets)) return false
            if (setter != other.setter) return false

            return true
        }

        override fun hashCode(): Int {
            var result = startIndex
            result = 31 * result + size
            result = 31 * result + alias.hashCode()
            result = 31 * result + fieldNames.contentHashCode()
            result = 31 * result + fieldOffsets.contentHashCode()
            result = 31 * result + setter.hashCode()
            return result
        }
    }
}