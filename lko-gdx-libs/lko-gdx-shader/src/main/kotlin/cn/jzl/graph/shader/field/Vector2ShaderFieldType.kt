package cn.jzl.graph.shader.field

import cn.jzl.graph.render.field.Vector2Type
import com.badlogic.gdx.math.Vector2

internal object Vector2ShaderFieldType : ShaderFieldType<FieldOutput> {
    override val realFieldType = Vector2Type
    override val fieldType: String = "vec2"
    override fun accepts(value: Any): Boolean = value is Vector2
}