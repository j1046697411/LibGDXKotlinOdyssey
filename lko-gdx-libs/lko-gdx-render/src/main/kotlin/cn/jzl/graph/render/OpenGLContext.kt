package cn.jzl.graph.render

import com.badlogic.gdx.graphics.GLTexture
import com.badlogic.gdx.graphics.g3d.utils.TextureDescriptor

interface OpenGLContext {
    fun begin()

    fun end()

    fun setDepthMask(depthMask: Boolean)

    fun setDepthTest(depthFunction: Int)

    fun setDepthTest(depthFunction: Int, depthRangeNear: Float, depthRangeFar: Float)

    fun setBlending(enabled: Boolean, sFactor: Int, dFactor: Int)

    fun setBlendingSeparate(enabled: Boolean, sFactor: Int, dFactor: Int, sFactorAlpha: Int, dFactorAlpha: Int)

    fun setCullFace(face: Int)

    fun bindTexture(textureDescriptor: TextureDescriptor<*>): Int

    fun bindTexture(texture: GLTexture): Int
}

