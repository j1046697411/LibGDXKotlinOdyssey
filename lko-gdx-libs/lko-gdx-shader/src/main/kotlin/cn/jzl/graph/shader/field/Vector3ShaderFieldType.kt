package cn.jzl.graph.shader.field

import cn.jzl.graph.render.field.Vector3Type
import com.badlogic.gdx.math.Vector3

internal object Vector3ShaderFieldType : ShaderFieldType<FieldOutput> {
    override val realFieldType = Vector3Type
    override val fieldType: String = "vec3"
    override fun accepts(value: Any): Boolean = value is Vector3
}