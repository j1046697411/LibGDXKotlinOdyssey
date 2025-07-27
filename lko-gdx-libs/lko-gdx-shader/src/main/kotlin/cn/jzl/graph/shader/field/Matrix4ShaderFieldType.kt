package cn.jzl.graph.shader.field

import cn.jzl.graph.render.field.Matrix4Type
import com.badlogic.gdx.math.Matrix4

internal object Matrix4ShaderFieldType : ShaderFieldType<FieldOutput> {
    override val realFieldType = Matrix4Type
    override val fieldType: String = "mat4"
    override fun accepts(value: Any): Boolean = value is Matrix4
}