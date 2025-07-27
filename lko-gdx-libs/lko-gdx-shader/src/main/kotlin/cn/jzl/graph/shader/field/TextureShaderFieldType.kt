package cn.jzl.graph.shader.field

import cn.jzl.graph.render.field.TextureType
import com.badlogic.gdx.graphics.Texture

internal object TextureShaderFieldType : ShaderFieldType<FieldOutput> {
    override val realFieldType = TextureType
    override val fieldType: String = "sampler2D"
    override fun accepts(value: Any): Boolean = value is Texture
}